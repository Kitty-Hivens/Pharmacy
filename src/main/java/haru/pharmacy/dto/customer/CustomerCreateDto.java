package haru.pharmacy.dto.customer;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating a customer card.
 * Validates phone format and discount range (0-100%).
 */
public record CustomerCreateDto(
        @NotNull(message = "{validation.required}")
        @DecimalMin(value = "0.00", message = "{validation.customer.discount.min}")
        @DecimalMax(value = "100.00", message = "{validation.customer.discount.max}")
        BigDecimal discountRate,

        @NotBlank(message = "{validation.required}")
        String firstName,

        @NotBlank(message = "{validation.required}")
        String lastName,

        @NotBlank(message = "{validation.required}")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "{validation.customer.phone.invalid}")
        String phone
) {}
