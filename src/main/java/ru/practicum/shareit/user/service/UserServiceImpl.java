package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EmailAlreadyExists;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto add(User user) {
        return UserMapper.toUserDto(repository.add(user).orElseThrow(() -> new EmailAlreadyExists("email"
                + user.getEmail() + " уже используется")));
    }

    @Override
    public UserDto get(int id) {
        return UserMapper.toUserDto(repository.get(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует")));
    }

    @Override
    public UserDto update(int id, UpdateUserDto user) {
        repository.get(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        return UserMapper.toUserDto(repository.update(id, user).orElseThrow(() -> new EmailAlreadyExists("email"
                + user.getEmail() + " уже используется")));
    }

    @Override
    public void remove(int id) {
        repository.get(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        repository.remove(id);
    }
}
