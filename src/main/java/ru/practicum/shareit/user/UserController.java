package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody UserDto user) {
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public User editUser(@PathVariable int id, @RequestBody UserDto user) {
        return userService.edit(id, user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }
}