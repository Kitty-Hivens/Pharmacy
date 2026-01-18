package haru.pharmacy.service;

import haru.pharmacy.dto.SaleCreateDto;
import haru.pharmacy.dto.SaleResponseDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
import haru.pharmacy.model.*;
import haru.pharmacy.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for processing sale operations.
 * <p>
 * Implements the core business logic for stock deduction
 * according to the FEFO (First Expired, First Out) strategy.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final MedicineRepository medicineRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Creates a new sale, deducting items from the stock.
     * <p>
     * The method is executed within a transaction. If an error occurs during the processing
     * of any item (insufficient stock, DB error), the entire transaction will be rolled back.
     * </p>
     * Deduction Algorithm (FEFO):
     * <ol>
     * <li>For each product, batches (Inventory) are searched, sorted by expiration date (ascending).</li>
     * <li>The total availability of the product across all batches is checked.</li>
     * <li>Iterative deduction of the quantity occurs from the "oldest" batch to the newer ones.</li>
     * </ol>
     *
     * @param dto      DTO containing sale data (customer, list of items, and quantities).
     * @param username The username (pharmacist/admin) performing the sale.
     * @throws ResourceNotFoundException   If the user, customer, or medicine is not found.
     * @throws BusinessConstraintException If there is insufficient stock to satisfy the request.
     */
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
            // FEFO strategy: find batches ordered by expiration date ASC
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
                    // Deplete this batch and move to the next one
                    batch.setStockQuantity(0);
                    quantityToSell -= availableInBatch;
                }
                // Save updated batch state (Optimistic Locking @Version check happens here implicitly)
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

    /**
     * Retrieves a paginated list of sales history.
     */
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> getAllSales(Pageable pageable) {
        Page<Sale> salesPage = saleRepository.findAll(pageable);
        return salesPage.map(this::mapToDto);
    }

    private SaleResponseDto mapToDto(Sale sale) {
        String sellerName = (sale.getEmployee() != null)
                ? sale.getEmployee().getFirstName() + " " + sale.getEmployee().getLastName()
                : "Unknown";

        // Обработка случая, если клиента удалили или это анонимная продажа
        String customerName = (sale.getCustomer() != null)
                ? sale.getCustomer().getFirstName() + " " + sale.getCustomer().getLastName()
                : "Guest";

        List<SaleResponseDto.SaleItemDto> itemDtos = sale.getItems().stream()
                .map(item -> new SaleResponseDto.SaleItemDto(
                        item.getMedicine().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        return new SaleResponseDto(
                sale.getId(),
                sale.getSaleDateTime(),
                sellerName,
                customerName,
                sale.getTotalAmount(),
                itemDtos
        );
    }
}
