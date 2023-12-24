package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    UserDto userDto;
    User user;
    User updateUser;
    UserDto updateUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("Test", "test@test.com");

        user = new User();
        user.setId(0);
        user.setName("Test");
        user.setEmail("test@test.com");

        updateUserDto = new UserDto();
        updateUser = new User();
    }

    @Test
    void create() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        User getUser = userService.create(userDto);

        assertEquals(getUser, user);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void editName() {
        updateUserDto.setName("UpdateName");
        updateUser.setName(updateUserDto.getName());
        updateUser.setEmail(user.getEmail());
        updateUser.setId(user.getId());
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(updateUser);

        User getUser = userService.edit(user.getId(), updateUserDto);

        assertEquals(getUser.getName(), updateUserDto.getName());
        assertEquals(getUser.getEmail(), user.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void editMail() {
        updateUserDto.setEmail("UpdateEmail@yandex.ru");
        updateUser.setName(user.getName());
        updateUser.setEmail(updateUserDto.getEmail());
        updateUser.setId(user.getId());
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(any(User.class))).thenReturn(updateUser);

        User getUser = userService.edit(user.getId(), updateUserDto);

        assertEquals(getUser.getName(), user.getName());
        assertEquals(getUser.getEmail(), updateUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void editNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class, () -> userService.edit(1, updateUserDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void get() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));

        User getUser = userService.get(0);

        assertEquals(getUser, user);
        verify(userRepository).findById(anyInt());
    }

    @Test
    void getNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class, () -> userService.get(0));

    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user, updateUser));

        List<User> users = userService.getAllUsers();
        assertEquals(users.size(), 2);
    }

    @Test
    void delete() {
        userService.delete(0);
        verify(userRepository, times(1)).deleteById(0);
    }
}