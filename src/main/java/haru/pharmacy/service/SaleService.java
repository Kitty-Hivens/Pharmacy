package haru.pharmacy.service;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.model.*;
import haru.pharmacy.repository.*;
// (Создай этот интерфейс или удали импорт, если пока без него)
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final MedicineRepository medicineRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void createSale(SaleCreateDto dto, String username) {
        // Create empty sale
        Sale sale = new Sale();
        sale.setSaleDateTime(LocalDateTime.now());

        // Get seller (or admin)
        UserAccount user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        sale.setEmployee(user.getEmployee());

        // Get customer
        if (dto.customerId() != null) {
            Customer customer = customerRepository.findById(dto.customerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            sale.setCustomer(customer);
        }

        // Create list of sales
        List<SaleItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleCreateDto.SaleItemRequest itemRequest : dto.items()) {
            Medicine medicine = medicineRepository.findById(itemRequest.medicineId())
                    .orElseThrow(() -> new RuntimeException("Medicine not found: " + itemRequest.medicineId()));

            int quantityToSell = itemRequest.quantity();

            // 1. Ищем все партии этого лекарства (сначала старые)
            List<Inventory> batches = inventoryRepository.findByMedicineIdOrderByExpirationDateAsc(medicine.getId());

            // Считаем, сколько всего есть на складе
            int totalStock = batches.stream().mapToInt(Inventory::getStockQuantity).sum();

            if (totalStock < quantityToSell) {
                throw new RuntimeException("Недостаточно товара на складе! Нужно: " + quantityToSell + ", Есть: " + totalStock);
            }

            // 2. Списываем по очереди (FEFO)
            for (Inventory batch : batches) {
                if (quantityToSell <= 0) break; // Всё продали

                int availableInBatch = batch.getStockQuantity();

                if (availableInBatch >= quantityToSell) {
                    // В этой партии хватает. Забираем сколько надо.
                    batch.setStockQuantity(availableInBatch - quantityToSell);
                    quantityToSell = 0;
                } else {
                    // В этой партии мало. Забираем всё и идем к следующей.
                    batch.setStockQuantity(0);
                    quantityToSell -= availableInBatch;
                }
                inventoryRepository.save(batch); // Сохраняем измененный остаток
            }


            SaleItem saleItem = new SaleItem();
            saleItem.setMedicine(medicine);
            saleItem.setQuantity(itemRequest.quantity());
            saleItem.setUnitPrice(medicine.getPrice());
            saleItem.setSale(sale);

            items.add(saleItem);

            BigDecimal lineTotal = medicine.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        sale.setItems(items);
        sale.setTotalAmount(totalAmount);

        saleRepository.save(sale);
    }
}