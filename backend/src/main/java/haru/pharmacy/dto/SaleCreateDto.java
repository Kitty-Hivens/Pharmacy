package haru.pharmacy.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * DTO for processing sales.
 * Contains a nested list of products, which is also validated.
 */
public record SaleCreateDto(
        Long customerId,

        @NotEmpty(message = "{validation.required}")
        List<@Valid SaleItemRequest> items
) {
    public record SaleItemRequest(
            @NotNull(message = "{validation.required}")
            Long medicineId,

            @NotNull(message = "{validation.required}")
            @Positive(message = "{validation.positive}")
            Integer quantity
    ) {}
}
