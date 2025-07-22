package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            return UserMapper.toUserDto(repository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExists("email=" + user.getEmail() + " уже есть");
        }

    }

    @Override
    public UserDto get(int id) {
        return UserMapper.toUserDto(repository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует")));
    }

    @Override
    public UserDto update(int id, UpdateUserDto newUser) {
        User user = repository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        if (newUser.getName() != null && !newUser.getName().isBlank()) {
            user.setName(newUser.getName());
        }
        if (newUser.getEmail() != null && !newUser.getEmail().isBlank()) {
            user.setEmail(newUser.getEmail());
        }
        try {
            return UserMapper.toUserDto(repository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExists("email=" + user.getEmail() + " уже есть");
        }
    }

    @Override
    public void remove(int id) {
        repository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователя с id = " + id + " не существует"));
        repository.deleteById(id);
    }
}
