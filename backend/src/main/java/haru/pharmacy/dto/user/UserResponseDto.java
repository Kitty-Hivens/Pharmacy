package haru.pharmacy.dto.user;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username,
        String role
) {}