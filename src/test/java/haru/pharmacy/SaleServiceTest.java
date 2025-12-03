package haru.pharmacy;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.model.*;
import haru.pharmacy.repository.*;
import haru.pharmacy.service.SaleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock private SaleRepository saleRepository;
    @Mock private MedicineRepository medicineRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private UserRepository userRepository;
    @Mock private InventoryRepository inventoryRepository;

    @InjectMocks
    private SaleService saleService;

    @Test
    void createSale_ShouldThrowException_WhenNotEnoughStock() {
        String username = "admin";


        SaleCreateDto dto = new SaleCreateDto(
                1L, 
                List.of(new SaleCreateDto.SaleItemRequest(1L, 10))
        );

        UserAccount mockUser = new UserAccount();
        mockUser.setEmployee(new Employee());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(new Customer()));

        Medicine mockMed = new Medicine();
        mockMed.setId(1L);
        mockMed.setName("Аспирин");
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(mockMed));

        Inventory batch = new Inventory();
        batch.setStockQuantity(5);
        when(inventoryRepository.findByMedicineIdOrderByExpirationDateAsc(any()))
                .thenReturn(List.of(batch));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> saleService.createSale(dto, username));

        String message = exception.getMessage();
        System.out.println("Поймана ошибка: " + message);
        
        assertTrue(message.contains("Недостаточно товара"));
        assertTrue(message.contains("Нужно: 10"));
        assertTrue(message.contains("Есть: 5"));


        verify(saleRepository, never()).save(any());
    }
}