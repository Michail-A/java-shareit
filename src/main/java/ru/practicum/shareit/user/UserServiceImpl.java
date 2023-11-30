package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exception.EmailException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User create(UserDto userDto) {
        User user = userRepository.save(UserMapper.mapToNewUser(userDto));
        return user;
    }

    @Override
    @Transactional
    public User edit(int id, UserDto userDto) {
        User newUser = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + id + " не найден."));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            newUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            newUser.setEmail(userDto.getEmail());
        }
        try {
            return userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new EmailException("Такой mail уже есть");
        }
    }

    @Override
    public User get(int id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

}
