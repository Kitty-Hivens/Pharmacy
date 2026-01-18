package haru.pharmacy.controller;

import haru.pharmacy.controller.request.AuthRequest;
import haru.pharmacy.controller.request.AuthResponse;
import haru.pharmacy.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and Token Management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", operationId = "login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        return authService.login(req.username(), req.password());
    }
}
