package haru.pharmacy.service;

import haru.pharmacy.controller.request.AuthResponse;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public AuthResponse login(String username, String password) {
        UserAccount user = userRepo.findByUsername(username)
                .orElseThrow(() -> new BusinessConstraintException("error.auth.invalid"));

        if (!user.getIsActive())
            throw new BusinessConstraintException("error.auth.invalid");

        if (!encoder.matches(password, user.getPasswordHash()))
            throw new BusinessConstraintException("error.auth.invalid");

        String token = jwtService.generateToken(user);

        return new AuthResponse(token, user.getRole().name());
    }
}
