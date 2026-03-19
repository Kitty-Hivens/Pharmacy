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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        // guard against empty cart — backend should never persist a zero-item sale
        if (dto.items() == null || dto.items().isEmpty()) {
            throw new BusinessConstraintException("error.sale.empty_items");
        }

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

            // Strict FEFO (checks expiration date)
            List<Inventory> batches = inventoryRepository.findValidBatchesForSale(medicine.getId(), LocalDate.now());

            // Calculate total VALID stock
            int totalValidStock = batches.stream().mapToInt(Inventory::getStockQuantity).sum();

            if (totalValidStock < quantityToSell) {
                throw new BusinessConstraintException("error.inventory.insufficient",
                        medicine.getName(), quantityToSell, totalValidStock);
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

        if (sale.getCustomer() != null && sale.getCustomer().getDiscountRate() != null) {
            BigDecimal discountAmount = totalAmount
                    .multiply(sale.getCustomer().getDiscountRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.subtract(discountAmount);
        }

        sale.setItems(items);
        sale.setTotalAmount(totalAmount);

        saleRepository.save(sale);
    }

    /**
     * Retrieves a paginated list of sales history with optional date filtering.
     */
    @Transactional(readOnly = true)
    public Page<SaleResponseDto> getAllSales(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<Sale> salesPage = saleRepository.findAllWithFilter(from, to, pageable);
        return salesPage.map(this::mapToDto);
    }

    private SaleResponseDto mapToDto(Sale sale) {
        String sellerName = (sale.getEmployee() != null)
                ? String.format("%s %s",
                Objects.toString(sale.getEmployee().getFirstName(), ""),
                Objects.toString(sale.getEmployee().getLastName(), "")).trim()
                : "Unknown";

        // Handling the case if a client has been deleted or this is an anonymous sale
        String customerName = (sale.getCustomer() != null)
                ? String.format("%s %s",
                Objects.toString(sale.getCustomer().getFirstName(), ""),
                Objects.toString(sale.getCustomer().getLastName(), "")).trim()
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
