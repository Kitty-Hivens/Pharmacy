package haru.pharmacy.dto.customer;

import java.math.BigDecimal;

public record CustomerCreateDto(
        BigDecimal discountRate,
        String firstName,
        String lastName,
        String phone
) {}
