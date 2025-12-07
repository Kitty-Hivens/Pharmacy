package haru.pharmacy;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.model.Inventory;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.model.Supplier;
import haru.pharmacy.repository.InventoryRepository;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.repository.SupplierRepository;
import haru.pharmacy.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link InventoryService}.
 * <p>
 * Verifies the logic for adding stock to the inventory, including
 * validation of related entities (Medicine, Supplier).
 *
 * @author Haru
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock private InventoryRepository inventoryRepository;
    @Mock private MedicineRepository medicineRepository;
    @Mock private SupplierRepository supplierRepository;

    @InjectMocks
    private InventoryService inventoryService;

    /**
     * Verifies that stock is successfully added when Medicine and Supplier exist.
     */
    @Test
    @DisplayName("Should add stock when Medicine and Supplier exist")
    void addStock_ShouldSave_WhenEntitiesExist() {
        // Given
        Long medicineId = 1L;
        Long supplierId = 10L;
        InventoryAddDto dto = new InventoryAddDto(
                medicineId, supplierId, 100, "BATCH-001", LocalDate.now().plusYears(1)
        );

        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(new Medicine()));
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(new Supplier()));

        // When
        inventoryService.addStock(dto);

        // Then
        verify(inventoryRepository).save(any(Inventory.class));
    }

    /**
     * Verifies that stock is successfully added when Supplier is not provided (null).
     */
    @Test
    @DisplayName("Should add stock without Supplier")
    void addStock_ShouldSave_WhenSupplierIsNull() {
        // Given
        Long medicineId = 1L;
        InventoryAddDto dto = new InventoryAddDto(
                medicineId, null, 50, "BATCH-002", LocalDate.now().plusYears(1)
        );

        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(new Medicine()));

        // When
        inventoryService.addStock(dto);

        // Then
        verify(inventoryRepository).save(any(Inventory.class));
        verify(supplierRepository, never()).findById(any());
    }

    /**
     * Verifies that exception is thrown if Medicine does not exist.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Medicine not found")
    void addStock_ShouldThrow_WhenMedicineMissing() {
        // Given
        InventoryAddDto dto = new InventoryAddDto(99L, null, 10, "B", LocalDate.now());
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addStock(dto));
        verify(inventoryRepository, never()).save(any());
    }

    /**
     * Verifies that exception is thrown if Supplier ID is provided but not found.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Supplier not found")
    void addStock_ShouldThrow_WhenSupplierMissing() {
        // Given
        Long medicineId = 1L;
        Long supplierId = 99L;
        InventoryAddDto dto = new InventoryAddDto(medicineId, supplierId, 10, "B", LocalDate.now());

        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(new Medicine()));
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addStock(dto));
        verify(inventoryRepository, never()).save(any());
    }
}
