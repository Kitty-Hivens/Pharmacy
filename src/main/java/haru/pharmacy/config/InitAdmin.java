package haru.pharmacy.config;

import haru.pharmacy.model.Employee;
import haru.pharmacy.model.UserAccount;
import haru.pharmacy.repository.EmployeeRepository;
import haru.pharmacy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class InitAdmin {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void init() {
        System.out.println(">>> ЗАПУСК INIT ADMIN <<<"); // Лог для проверки

        if (userRepo.count() == 0) {
            System.out.println(">>> БАЗА ПУСТАЯ, СОЗДАЮ АДМИНА... <<<");

            // 1. Создаем сотрудника
            Employee emp = new Employee();
            emp.setFirstName("Главный");
            emp.setLastName("Админ");
            emp.setPosition("Директор");
            emp.setHireDate(LocalDate.now());
            emp = employeeRepo.save(emp); // Сохраняем и получаем ID

            // 2. Создаем аккаунт
            UserAccount user = new UserAccount();
            user.setUsername("admin");
            user.setPasswordHash(encoder.encode("12345"));
            user.setRole("ADMIN");
            user.setIsActive(true);
            user.setEmployee(emp); // Привязываем сотрудника!

            userRepo.save(user);
            System.out.println(">>> АДМИН УСПЕШНО СОЗДАН! <<<");
        } else {
            System.out.println(">>> ПОЛЬЗОВАТЕЛИ УЖЕ ЕСТЬ. ПРОПУСК. <<<");
        }
    }
}