package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    User create(UserDto user);
    User edit(int id, UserDto user);
    User get(int id);
    List<User> getAllUsers();
    void delete(int id);
}
