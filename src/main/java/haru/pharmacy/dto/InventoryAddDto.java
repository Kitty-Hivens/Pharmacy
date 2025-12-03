package haru.pharmacy.dto;

import java.time.LocalDate;

public record InventoryAddDto(
        Long medicineId,
        Long supplierId,
        Integer quantity,
        String batchNumber,
        LocalDate expirationDate
) {}