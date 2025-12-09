package haru.pharmacy.dto.medicine;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating a new medicine.
 * Contains basic checks for a positive price and the presence of a name.
 */
public record MedicineCreateDto(
        @NotBlank(message = "{validation.required}")
        @Size(min = 2, max = 100, message = "{validation.medicine.name.size}")
        String name,

        @NotNull(message = "{validation.required}")
        @Positive(message = "{validation.positive}")
        BigDecimal price,

        @NotBlank(message = "{validation.required}")
        String manufacturer,

        String description,

        @NotNull(message = "{validation.required}")
        Boolean prescriptionRequired
) {}
