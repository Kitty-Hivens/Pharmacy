package haru.pharmacy.service.impl;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.mapper.UserMapper; // <--- Импорт маппера
import haru.pharmacy.model.Employee;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.EmployeeRepository;
import haru.pharmacy.repository.UserRepository;
import haru.pharmacy.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    @Transactional
    public void createUser(UserCreateDto dto) {
        if (userRepo.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        Employee emp = mapper.toEmployee(dto);
        emp.setHireDate(LocalDate.now());
        emp = employeeRepo.save(emp);

        UserAccount user = new UserAccount();
        user.setUsername(dto.username());
        user.setPasswordHash(encoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setIsActive(true);
        user.setEmployee(emp);

        userRepo.save(user);
    }

    public List<UserResponseDto> getAll() {
        return userRepo.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void delete(Long id) {
        UserAccount user = userRepo.findById(id).orElseThrow();
        userRepo.delete(user);
    }
}