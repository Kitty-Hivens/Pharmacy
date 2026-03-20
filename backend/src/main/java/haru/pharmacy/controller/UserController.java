package haru.pharmacy.controller;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Employee and User Management")
public class UserController {

    private final UserService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register a new user", operationId = "registerUser")
    public ResponseEntity<Void> register(@Valid @RequestBody UserCreateDto dto) {
        service.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", operationId = "getAllUsers")
    public List<UserResponseDto> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", operationId = "deleteUser") // Keeps frontend clean
    public ResponseEntity<Void> fire(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
