package haru.pharmacy.repository;

import haru.pharmacy.model.Sale;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Finds all sales ordered by date descending.
     * Uses EntityGraph to eagerly fetch related entities.
     */
    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.medicine"})
    List<Sale> findAllByOrderBySaleDateTimeDesc();

    /**
     * Retrieves all sales within a specified date range with pagination.
     * <p>
     * Parameters are optional. If a parameter is null, that boundary is ignored.
     * Uses EntityGraph to prevent N+1 queries.
     * </p>
     *
     * @param from     Start date-time (inclusive). Can be null.
     * @param to       End date-time (inclusive). Can be null.
     * @param pageable Pagination and sorting options.
     * @return A page of sales matching the criteria.
     */
    @EntityGraph(attributePaths = {"employee", "customer"})
    @Query("""
        SELECT s FROM Sale s
        WHERE (:from IS NULL OR s.saleDateTime >= :from)
        AND (:to IS NULL OR s.saleDateTime <= :to)
    """)
    Page<Sale> findAllWithFilter(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    /**
     * Overrides standard findAll to ensure EntityGraph is applied.
     */
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "customer"})
    Page<Sale> findAll(@NonNull Pageable pageable);
}
