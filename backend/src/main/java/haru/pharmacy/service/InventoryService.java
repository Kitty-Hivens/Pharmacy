package haru.pharmacy.service;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.model.Inventory;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.model.Supplier;
import haru.pharmacy.repository.InventoryRepository;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing warehouse inventory.
 * <p>
 * Responsible for restocking (adding new batches of goods).
 * </p>
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;
    private final SupplierRepository supplierRepository;

    /**
     * Adds a new batch of goods to the inventory.
     * <p>
     * Creates a new record in the Inventory table. Even if the same product
     * with the same expiration date already exists, a new record (batch) is created,
     * as the batchNumber might differ.
     * </p>
     *
     * @param dto Data about the arrival (medicine ID, supplier, quantity, batch number, expiration date).
     * @throws ResourceNotFoundException If the specified medicine or supplier is not found in the DB.
     */
    @Transactional
    public void addStock(InventoryAddDto dto) {
        Medicine medicine = medicineRepository.findById(dto.medicineId())
                .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found"));

        Supplier supplier = null;
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.supplier.not_found"));
        }

        Inventory inventory = new Inventory();
        inventory.setMedicine(medicine);
        inventory.setSupplier(supplier);
        inventory.setStockQuantity(dto.quantity());
        inventory.setBatchNumber(dto.batchNumber());
        inventory.setExpirationDate(dto.expirationDate());

        inventoryRepository.save(inventory);
    }
}
