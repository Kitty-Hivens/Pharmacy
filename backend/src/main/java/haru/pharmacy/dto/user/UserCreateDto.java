package haru.pharmacy.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for registering a new employee.
 * Role is restricted to ADMIN or PHARMACIST values.
 */
public record UserCreateDto(
        @NotBlank(message = "{validation.required}")
        String firstName,

        @NotBlank(message = "{validation.required}")
        String lastName,

        String position,

        @NotBlank(message = "{validation.required}")
        @Size(min = 4, max = 20, message = "{validation.user.login.size}")
        String username,

        @NotBlank(message = "{validation.required}")
        @Size(min = 6, message = "{validation.user.password.size}")
        String password,

        @Pattern(regexp = "^(ADMIN|PHARMACIST)$", message = "{validation.user.role.pattern}")
        String role
) {}