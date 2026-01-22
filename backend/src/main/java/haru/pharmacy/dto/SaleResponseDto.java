package haru.pharmacy.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SaleResponseDto(
        Long id,
        LocalDateTime dateTime,
        String sellerName,
        String customerName,
        BigDecimal totalAmount,
        List<SaleItemDto> items
) {
    public record SaleItemDto(
            String medicineName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalPrice
    ) {}
}
