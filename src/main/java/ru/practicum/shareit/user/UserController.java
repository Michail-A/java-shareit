package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody UserDto user) {
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserException(final UserException e) {
        return Map.of("error", "Ошибка пользователя",
                "message: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailException(final EmailException e) {
        return Map.of("error", "Такой email уже зарегистрирован",
                "message: ", e.getMessage());
    }
}
