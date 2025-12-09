package haru.pharmacy.service;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("error.seller.not_found"));
        sale.setEmployee(user.getEmployee());

        // Get customer
        if (dto.customerId() != null) {
            Customer customer = customerRepository.findById(dto.customerId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.customer.not_found"));
            sale.setCustomer(customer);
        }

        // Create list of sales
        List<SaleItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleCreateDto.SaleItemRequest itemRequest : dto.items()) {
            Medicine medicine = medicineRepository.findById(itemRequest.medicineId())
                    .orElseThrow(() -> new ResourceNotFoundException("error.medicine.not_found", itemRequest.medicineId()));

            int quantityToSell = itemRequest.quantity();
            List<Inventory> batches = inventoryRepository.findByMedicineIdOrderByExpirationDateAsc(medicine.getId());
            int totalStock = batches.stream().mapToInt(Inventory::getStockQuantity).sum();

            if (totalStock < quantityToSell) {
                throw new BusinessConstraintException("error.inventory.insufficient",
                        medicine.getName(), quantityToSell, totalStock);
            }

            for (Inventory batch : batches) {
                if (quantityToSell <= 0) break;

                int availableInBatch = batch.getStockQuantity();

                if (availableInBatch >= quantityToSell) {
                    batch.setStockQuantity(availableInBatch - quantityToSell);
                    quantityToSell = 0;
                } else {
                    batch.setStockQuantity(0);
                    quantityToSell -= availableInBatch;
                }
                inventoryRepository.save(batch);
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