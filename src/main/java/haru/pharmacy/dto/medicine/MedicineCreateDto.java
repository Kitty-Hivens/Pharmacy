package haru.pharmacy.dto.medicine;

import java.math.BigDecimal;

public record MedicineCreateDto(
        String name,
        BigDecimal price,
        String manufacturer,
        String description,
        Boolean prescriptionRequired
) {}
