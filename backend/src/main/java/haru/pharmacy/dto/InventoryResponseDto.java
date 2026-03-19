package haru.pharmacy.dto;

import java.time.LocalDate;

/**
 * Public API representation of an Inventory batch.
 * Intentionally excludes the {@code version} field (optimistic locking detail)
 * which has no meaning for API consumers and leaks implementation internals.
 */
public record InventoryResponseDto(
        Long id,
        Long medicineId,
        String medicineName,
        String medicineManufacturer,
        Long supplierId,
        String supplierName,
        Integer stockQuantity,
        String batchNumber,
        LocalDate expirationDate
) {}
