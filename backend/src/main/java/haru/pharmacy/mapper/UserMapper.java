package haru.pharmacy.mapper;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;
import haru.pharmacy.model.Employee;
import haru.pharmacy.model.Role;
import haru.pharmacy.model.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    Employee toEmployee(UserCreateDto dto);

    @Mapping(source = "employee.firstName", target = "firstName")
    @Mapping(source = "employee.lastName", target = "lastName")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    UserResponseDto toDto(UserAccount user);

    @SuppressWarnings("unused")
    @Named("roleToString")
    default String roleToString(Role role) {
        return role != null ? role.name() : null;
    }
}
