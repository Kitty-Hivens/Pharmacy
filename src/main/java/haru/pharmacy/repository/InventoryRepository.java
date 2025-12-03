package haru.pharmacy.repository;

import haru.pharmacy.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByMedicineIdOrderByExpirationDateAsc(Long medicineId);
}