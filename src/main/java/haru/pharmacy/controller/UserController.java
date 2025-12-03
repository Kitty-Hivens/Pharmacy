package haru.pharmacy.controller;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public void register(@RequestBody UserCreateDto dto) {
        service.createUser(dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponseDto> getAll() {
        return service.getAll();
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void fire(@PathVariable Long id) {
        service.delete(id);
    }
}
