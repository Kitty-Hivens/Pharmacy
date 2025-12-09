package haru.pharmacy;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.exception.BusinessConstraintException;
import haru.pharmacy.exception.ResourceNotFoundException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserServiceImpl}.
 * <p>
 * Verifies user registration logic, password encryption,
 * duplicate login prevention, and administrative operations.
 *
 * @author Haru
 * @version 2.1
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private EmployeeRepository employeeRepo;
    @Mock private PasswordEncoder encoder;
    @Mock private UserMapper mapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("Create should save user when username is unique")
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

    @Test
    @DisplayName("Create should throw exception when username is taken")
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

    @Test
    @DisplayName("GetAll should return list of users")
    void getAll_ShouldReturnList() {
        // Given
        UserAccount user = new UserAccount();
        Employee emp = new Employee();
        emp.setFirstName("John");
        user.setEmployee(emp);

        UserResponseDto dto = new UserResponseDto(1L, "John", "Doe", "jdoe", "ADMIN");

        when(userRepo.findAll()).thenReturn(List.of(user));
        when(mapper.toDto(user)).thenReturn(dto);

        // When
        List<UserResponseDto> result = userService.getAll();

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("jdoe", result.getFirst().username());
    }

    @Test
    @DisplayName("Delete should remove user when exists")
    void delete_ShouldRemoveUser_WhenFound() {
        // Given
        Long id = 1L;
        UserAccount user = new UserAccount();
        when(userRepo.findById(id)).thenReturn(Optional.of(user));

        // When
        userService.delete(id);

        // Then
        verify(userRepo).delete(user);
    }

    @Test
    @DisplayName("Delete should throw exception when user not found")
    void delete_ShouldThrow_WhenNotFound() {
        // Given
        Long id = 99L;
        when(userRepo.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.delete(id));
        verify(userRepo, never()).delete(any());
    }
}
