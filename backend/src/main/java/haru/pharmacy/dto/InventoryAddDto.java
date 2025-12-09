package haru.pharmacy.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

/**
 * DTO for goods arrival at warehouse operation.
 * Requires expiration date to be in the future.
 */
public record InventoryAddDto(
        @NotNull(message = "{validation.required}")
        Long medicineId,

        Long supplierId,

        @NotNull(message = "{validation.required}")
        @Positive(message = "{validation.positive}")
        Integer quantity,

        @NotBlank(message = "{validation.required}")
        String batchNumber,

        @NotNull(message = "{validation.required}")
        @Future(message = "{validation.inventory.date.future}")
        LocalDate expirationDate
) {}