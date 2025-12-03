package haru.pharmacy.service;

import haru.pharmacy.controller.request.AuthResponse;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthResponse login(String username, String password) {
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid credentials");

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getRole());
    }
}
