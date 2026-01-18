package haru.pharmacy.repository;

import haru.pharmacy.model.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.medicine"})
    List<Sale> findAllByOrderBySaleDateTimeDesc();

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"employee", "customer"})
    Page<Sale> findAll(@NonNull Pageable pageable);
}
