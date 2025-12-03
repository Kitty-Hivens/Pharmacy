package haru.pharmacy.service.interfaces;

import haru.pharmacy.dto.user.UserCreateDto;
import haru.pharmacy.dto.user.UserResponseDto;

import java.util.List;

public interface UserService {

    void createUser(UserCreateDto dto);

    List<UserResponseDto> getAll();

    void delete(Long id);

}
