package haru.pharmacy.dto.customer;

import java.math.BigDecimal;

public record CustomerUpdateDto(
        BigDecimal discountRate,
        String firstName,
        String lastName,
        String phone
) {}

