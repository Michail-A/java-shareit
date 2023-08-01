package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public User create(UserDto user) {
        return userDao.create(user);
    }

    @Override
    public User edit(int id, UserDto user) {
        return userDao.edit(id, user);
    }

    @Override
    public User get(int id) {
        return userDao.getUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.getAll();
    }

    @Override
    public void delete(int id) {
        userDao.delete(id);
    }
}
