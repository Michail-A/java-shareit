package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping
    public UserDto add(@Valid @RequestBody User user) {
        return service.add(user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable int userId) {
        return service.get(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable int userId, @RequestBody UpdateUserDto user) {
        return service.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable int userId) {
        service.remove(userId);
    }
}
