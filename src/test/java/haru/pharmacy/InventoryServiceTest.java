package haru.pharmacy;

import haru.pharmacy.dto.InventoryAddDto;
import haru.pharmacy.model.Inventory;
import haru.pharmacy.model.Medicine;
import haru.pharmacy.repository.InventoryRepository;
import haru.pharmacy.repository.MedicineRepository;
import haru.pharmacy.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Подключаем библиотеку Mockito
class InventoryServiceTest {

    @Mock // Создай ФЕЙКОВЫЙ репозиторий (не лезь в реальную базу)
    private InventoryRepository inventoryRepository;

    @Mock // Создай ФЕЙКОВЫЙ репозиторий лекарств
    private MedicineRepository medicineRepository;

    @InjectMocks // Вставь эти фейки в настоящий сервис
    private InventoryService inventoryService;

    @Test
    void addStock_ShouldSaveInventory_WhenMedicineExists() {
        // --- 1. PREPARE (Готовим данные) ---
        Long medicineId = 1L;
        InventoryAddDto dto = new InventoryAddDto(
                medicineId, 100, "BATCH-1", LocalDate.now().plusYears(1)
        );

        Medicine mockMedicine = new Medicine();
        mockMedicine.setId(medicineId);
        mockMedicine.setName("Аспирин");

        // Учим фейк: "Если у тебя спросят ID 1, верни вот этот mockMedicine"
        when(medicineRepository.findById(medicineId)).thenReturn(Optional.of(mockMedicine));

        // --- 2. ACT (Выполняем действие) ---
        inventoryService.addStock(dto);

        // --- 3. ASSERT (Проверяем результат) ---
        // Проверяем: был ли вызван метод save у репозитория инвентаря ровно 1 раз?
        verify(inventoryRepository).save(any(Inventory.class));
        
        System.out.println("Тест прошел! Мы обманули сервис и он ничего не понял.");
    }
}