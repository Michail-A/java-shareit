package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;
    private UserDto createdUser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("test")
                .email("test@mail.ru")
                .build();

        // Сохраняем созданного пользователя
        createdUser = userService.add(user);
    }

    @Test
    void editName() {
        UpdateUserDto updatedUserDto = new UpdateUserDto();
        updatedUserDto.setName("UpdateName");

        UserDto userDto = userService.update(createdUser.getId(), updatedUserDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id= :id", User.class);
        User user = query.setParameter("id", createdUser.getId()).getSingleResult();

        assertEquals(userDto.getId(), createdUser.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void editEmail() {
        UpdateUserDto updatedUserDto = new UpdateUserDto();
        updatedUserDto.setEmail("update@mail.ru");

        userService.update(createdUser.getId(), updatedUserDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id= :id", User.class);
        User user = query.setParameter("id", createdUser.getId()).getSingleResult();

        assertEquals(user.getId(), createdUser.getId());
        assertEquals(user.getName(), createdUser.getName());
        assertEquals(user.getEmail(), updatedUserDto.getEmail());
    }
}