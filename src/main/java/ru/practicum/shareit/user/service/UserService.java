package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto add(User user);

    UserDto get(int id);

    UserDto update(int id, UpdateUserDto user);

    void remove(int id);
}
