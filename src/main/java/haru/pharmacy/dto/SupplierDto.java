package haru.pharmacy.dto;

public record SupplierDto(
        Long id,
        String name,
        String contactPerson,
        String email,
        String phone
) {}