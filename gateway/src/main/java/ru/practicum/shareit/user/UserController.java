package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto) {

        return client.createUser(userDto);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Object> editUser(@PathVariable int id, @RequestBody UserDto user) {
        return client.editUser(user, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {
        return client.getUser(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable int id) {
        return client.delete(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }
}
