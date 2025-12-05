package haru.pharmacy.service;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.model.Inventory;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.model.Supplier;
import haru.pharmacy.repository.InventoryRepository;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicineRepository medicineRepository;
    private final SupplierRepository supplierRepository;

    public void addStock(InventoryAddDto dto) {
        Medicine medicine = medicineRepository.findById(dto.medicineId())
                .orElseThrow(() -> new ResourceNotFoundException("Medicine not found"));

        Supplier supplier = null;
        if (dto.supplierId() != null) {
            supplier = supplierRepository.findById(dto.supplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found"));
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