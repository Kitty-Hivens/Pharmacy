package haru.pharmacy;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.mapper.UserMapper;
import haru.pharmacy.model.Employee;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.EmployeeRepository;
import haru.pharmacy.repository.UserRepository;
import haru.pharmacy.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 * <p>
 * Verifies user registration logic, password encryption,
 * and duplicate login prevention.
 *
 * @author Haru
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private EmployeeRepository employeeRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    /**
     * Verifies that a new user is successfully created when the username is unique.
     * <p>
     * <b>Expected Behavior:</b>
     * <ul>
     * <li>Password should be encoded.</li>
     * <li>Employee entity should be saved.</li>
     * <li>UserAccount entity should be saved.</li>
     * </ul>
     */
    @Test
    @DisplayName("Should create user when username is valid")
    void createUser_ShouldSaveUser_WhenUsernameIsUnique() {
        // Given
        UserCreateDto dto = new UserCreateDto(
                "John", "Doe", "Pharmacist",
                "jdoe", "secret123", "PHARMACIST"
        );
        
        when(userRepo.findByUsername(dto.username())).thenReturn(Optional.empty());
        when(mapper.toEmployee(dto)).thenReturn(new Employee());
        when(encoder.encode(dto.password())).thenReturn("encoded_pass");
        
        // When
        userService.createUser(dto);

        // Then
        verify(employeeRepo).save(any(Employee.class));
        verify(userRepo).save(any(UserAccount.class));
    }

    /**
     * Verifies that an exception is thrown when trying to register a user
     * with an existing username.
     */
    @Test
    @DisplayName("Should throw exception when username is taken")
    void createUser_ShouldThrow_WhenUsernameExists() {
        // Given
        UserCreateDto dto = new UserCreateDto(
                "John", "Doe", "Pos", "admin", "123", "ADMIN"
        );
        
        when(userRepo.findByUsername("admin")).thenReturn(Optional.of(new UserAccount()));

        // When & Then
        assertThrows(BusinessConstraintException.class, () -> userService.createUser(dto));
        
        verify(userRepo, never()).save(any());
    }
}
