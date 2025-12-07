package haru.pharmacy;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.model.*;
import haru.pharmacy.repository.*;
import haru.pharmacy.service.SaleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link SaleService}.
 * <p>
 * This class validates the business logic associated with processing sales,
 * including stock availability checks, the FEFO (First Expired, First Out)
 * inventory deduction algorithm, and entity validation.
 *
 * @author Haru
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private SaleService saleService;

    /**
     * Verifies that a sale is successfully processed when all entities exist
     * and there is sufficient stock.
     * <p>
     * Checks that the inventory stock is correctly deducted across multiple batches
     * according to the FEFO strategy and that the sale is persisted.
     */
    @Test
    @DisplayName("Should successfully create sale and deduct inventory stock")
    void createSale_ShouldSucceed_WhenStockIsSufficient() {
        // Given
        String username = "admin";
        Long medicineId = 1L;
        int requestedQty = 15;

        // Setup batches: Batch 1 has 10, Batch 2 has 20. Total 30.
        // We need 15. Batch 1 should become 0, Batch 2 should become 15.
        Inventory batch1 = new Inventory();
        batch1.setId(101L);
        batch1.setStockQuantity(10);

        Inventory batch2 = new Inventory();
        batch2.setId(102L);
        batch2.setStockQuantity(20);

        SaleCreateDto dto = new SaleCreateDto(
                1L,
                List.of(new SaleCreateDto.SaleItemRequest(medicineId, requestedQty))
        );

        UserAccount mockUser = new UserAccount();
        mockUser.setEmployee(new Employee());

        Medicine mockMedicine = new Medicine();
        mockMedicine.setId(medicineId);
        mockMedicine.setPrice(BigDecimal.TEN);
        mockMedicine.setName("Aspirin");

        // Mock behaviors
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(mockMedicine));
        when(inventoryRepository.findByMedicineIdOrderByExpirationDateAsc(medicineId))
                .thenReturn(List.of(batch1, batch2));

        // When
        saleService.createSale(dto, username);

        // Then
        // Verify sale persistence
        verify(saleRepository).save(any(Sale.class));

        // Verify inventory updates
        assertEquals(0, batch1.getStockQuantity(), "First batch should be fully depleted");
        assertEquals(15, batch2.getStockQuantity(), "Second batch should be partially depleted");

        // Verify that save was called for both updated batches
        verify(inventoryRepository, times(2)).save(any(Inventory.class));
    }

    /**
     * Verifies that a {@link BusinessConstraintException} is thrown when the
     * total available stock is less than the requested quantity.
     */
    @Test
    @DisplayName("Should throw BusinessConstraintException when stock is insufficient")
    void createSale_ShouldThrow_WhenNotEnoughStock() {
        // Given
        String username = "admin";
        Long medicineId = 1L;
        int requestedQty = 10;
        int availableQty = 5;

        SaleCreateDto dto = new SaleCreateDto(
                null, // No customer
                List.of(new SaleCreateDto.SaleItemRequest(medicineId, requestedQty))
        );

        Inventory batch = new Inventory();
        batch.setStockQuantity(availableQty);

        Medicine mockMedicine = new Medicine();
        mockMedicine.setId(medicineId);
        mockMedicine.setName("Aspirin");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserAccount()));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(mockMedicine));
        when(inventoryRepository.findByMedicineIdOrderByExpirationDateAsc(medicineId))
                .thenReturn(List.of(batch));

        // When & Then
        assertThrows(BusinessConstraintException.class, () -> saleService.createSale(dto, username));

        // Ensure no data was saved
        verify(saleRepository, never()).save(any());
        verify(inventoryRepository, never()).save(any());
    }

    /**
     * Verifies that a {@link ResourceNotFoundException} is thrown if the user
     * initiating the sale cannot be found.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Seller is not found")
    void createSale_ShouldThrow_WhenSellerNotFound() {
        // Given
        String username = "unknown_user";
        SaleCreateDto dto = new SaleCreateDto(null, Collections.emptyList());

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(dto, username));
    }

    /**
     * Verifies that a {@link ResourceNotFoundException} is thrown if the customer
     * ID provided in the DTO does not correspond to an existing customer.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Customer is not found")
    void createSale_ShouldThrow_WhenCustomerNotFound() {
        // Given
        String username = "admin";
        Long customerId = 999L;
        SaleCreateDto dto = new SaleCreateDto(customerId, Collections.emptyList());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserAccount()));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(dto, username));
    }

    /**
     * Verifies that a {@link ResourceNotFoundException} is thrown if a medicine
     * in the sale items list does not exist.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Medicine is not found")
    void createSale_ShouldThrow_WhenMedicineNotFound() {
        // Given
        String username = "admin";
        Long medicineId = 999L;
        SaleCreateDto dto = new SaleCreateDto(
                null,
                List.of(new SaleCreateDto.SaleItemRequest(medicineId, 1))
        );

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserAccount()));
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(dto, username));
    }
}