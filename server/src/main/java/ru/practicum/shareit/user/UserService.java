package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(UserDto userDto);

    User edit(int id, UserDto userDto);

    User get(int id);

    List<User> getAllUsers();

    void delete(int id);
}
