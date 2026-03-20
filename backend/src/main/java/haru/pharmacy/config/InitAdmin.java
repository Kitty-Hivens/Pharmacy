package haru.pharmacy.config;

import haru.pharmacy.model.Employee;
import haru.pharmacy.model.Role;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.EmployeeRepository;
import haru.pharmacy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitAdmin {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;

    @Value("${admin.initial.password:#{null}}")
    private String configuredPassword;

    @PostConstruct
    @Transactional
    public void init() {
        String adminUsername = "admin";

        if (userRepo.findByUsername(adminUsername).isPresent()) {
            log.info("Admin user '{}' already exists. Skipping initialization.", adminUsername);
            return;
        }

        log.info("Initializing default admin user '{}'...", adminUsername);

        Employee emp = new Employee();
        emp.setFirstName("Super");
        emp.setLastName("Admin");
        emp.setPosition("System Administrator");
        emp.setHireDate(LocalDate.now());
        emp = employeeRepo.save(emp);

        String passwordToUse = configuredPassword;
        if (passwordToUse == null || passwordToUse.isBlank()) {
            passwordToUse = UUID.randomUUID().toString().substring(0, 12);
            log.warn("Admin password was not configured via ADMIN_INITIAL_PASSWORD env variable. " +
                    "A random password has been generated — retrieve it from the DB or set the env variable and restart.");
        } else {
            log.info("Using configured admin password from environment.");
        }

        UserAccount user = new UserAccount();
        user.setUsername(adminUsername);
        user.setPasswordHash(encoder.encode(passwordToUse));
        user.setRole(Role.ADMIN);
        user.setIsActive(true);
        user.setEmployee(emp);

        userRepo.save(user);
        log.info("Admin user created successfully.");
    }
}
