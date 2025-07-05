package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> add(User user);

    Optional<User> get(int id);

    Optional<User> update(int id, UpdateUserDto user);

    void remove(int id);
}
