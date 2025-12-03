package haru.pharmacy.dto.customer;

import java.math.BigDecimal;

public record CustomerResponseDto(
        Long id,
        BigDecimal discountRate,
        String firstName,
        String lastName,
        String phone
) {}
