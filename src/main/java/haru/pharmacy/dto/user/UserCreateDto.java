package haru.pharmacy.dto.user;

public record UserCreateDto(
        String firstName,
        String lastName,
        String position,
        String username,
        String password,
        String role
) {}