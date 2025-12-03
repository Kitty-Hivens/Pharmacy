package haru.pharmacy.dto;

import java.util.List;

public record SaleCreateDto(
        Long customerId,
        List<SaleItemRequest> items
) {
    public record SaleItemRequest(
            Long medicineId,
            Integer quantity
    ) {}
}