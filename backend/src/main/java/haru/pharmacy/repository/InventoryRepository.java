package haru.pharmacy.repository;

import haru.pharmacy.model.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for managing Inventory batches.
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    /**
     * Legacy method: Simple lookup by medicine.
     * Kept for backward compatibility.
     */
    @Deprecated
    List<Inventory> findByMedicineIdOrderByExpirationDateAsc(Long medicineId);

    /**
     * Finds valid batches for a specific medicine suitable for sale using the FEFO strategy.
     * <p>
     * Selection criteria:
     * 1. Matches the given Medicine ID.
     * 2. Stock quantity is greater than 0.
     * 3. Expiration date is today or in the future (not expired).
     * </p>
     *
     * @param medicineId  The ID of the medicine to sell.
     * @param currentDate The current reference date (usually today) to filter out expired goods.
     * @return A list of valid inventory batches, sorted by expiration date (ASC).
     */
    @Query("""
        SELECT i FROM Inventory i
        WHERE i.medicine.id = :medicineId
        AND i.stockQuantity > 0
        AND i.expirationDate >= :currentDate
        ORDER BY i.expirationDate ASC
    """)
    List<Inventory> findValidBatchesForSale(
            @Param("medicineId") Long medicineId,
            @Param("currentDate") LocalDate currentDate
    );

    /**
     * Searches for inventory items by Medicine Name or Batch Number.
     * <p>
     * This query is case-insensitive and allows partial matches.
     * If the search query is null or empty, the condition is ignored.
     * </p>
     *
     * @param query    The search string (e.g., "Aspirin" or "BATCH-001").
     * @param pageable Pagination information.
     * @return A page of matching inventory items.
     */
    @Query("""
        SELECT i FROM Inventory i
        WHERE (:query IS NULL OR :query = '')
        OR LOWER(i.medicine.name) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(i.batchNumber) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    Page<Inventory> searchByMedicineOrBatch(@Param("query") String query, Pageable pageable);
}
