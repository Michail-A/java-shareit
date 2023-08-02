package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    private HashMap<Integer, User> storage = new HashMap<>();

    private int id = 1;

    @Override
    public User create(UserDto user) {
        User newUser = new User(id, user.getName(), user.getEmail());
        validateExistsEmail(newUser);
        storage.put(newUser.getId(), newUser);
        id++;
        return newUser;
    }

    @Override
    public User edit(int userId, UserDto user) {
        User newUser = new User(userId, user.getName() != null ? user.getName() : storage.get(userId).getName(),
                user.getEmail() != null ? user.getEmail() : storage.get(userId).getEmail());
        validateExistsEmail(newUser);
        storage.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public void delete(int id) {
        storage.remove(id);
    }

    @Override
    public User getUser(int id) {
        return storage.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(storage.values());
    }

    private void validateExistsEmail(User user) {
        //проверка на уже существующий mail, после добавления БД собираюсь переделать на поиск по полю
        for (User savedUser : storage.values()) {
            if (savedUser.getEmail().equals(user.getEmail())) {
                if (savedUser.getId() != user.getId()) {
                    throw new EmailException("Пользователь с таким email уже есть");
                }
            }
        }
    }

    @Override
    public boolean checkUser(int userId) {
        return storage.containsKey(userId);
    }
}
