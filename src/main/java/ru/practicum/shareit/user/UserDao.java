package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserDao {
    User create(UserDto user);

    User edit(int id, UserDto user);

    void delete(int id);

    User getUser(int id);

    List<User> getAll();

    boolean checkUser(int userId);

}
