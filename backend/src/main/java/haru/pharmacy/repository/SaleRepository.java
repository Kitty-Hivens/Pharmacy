package haru.pharmacy.repository;

import haru.pharmacy.model.Sale;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    @EntityGraph(attributePaths = {"employee", "customer", "items", "items.medicine"})
    List<Sale> findAllByOrderBySaleDateTimeDesc();
}
