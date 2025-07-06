package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Integer, User> users = new HashMap<>();

    private final Map<String, User> emails = new HashMap<>();

    private int id = 1;

    @Override
    public Optional<User> add(User user) {
        if (emails.containsKey(user.getEmail())) {
            return Optional.empty();
        }
        user.setId(id);
        users.put(id, user);
        emails.put(user.getEmail(), user);
        id++;
        return Optional.of(user);
    }

    @Override
    public Optional<User> get(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> update(int id, UpdateUserDto user) {
        User updatedUser = users.get(id);
        if (user.getEmail() != null) {
            if (emails.containsKey(user.getEmail())) {
                return Optional.empty();
            }
            updatedUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        return Optional.of(updatedUser);
    }

    @Override
    public void remove(int id) {
        User user = users.get(id);
        users.remove(id);
        emails.remove(user.getEmail());
    }
}
