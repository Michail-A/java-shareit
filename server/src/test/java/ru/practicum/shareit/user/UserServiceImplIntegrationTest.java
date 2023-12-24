package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("Test", "test@test.com");
        userService.create(userDto);
    }

    @Test
    void editName() {
        userDto.setName("UpdateName");
        userService.edit(1, userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id= :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();

        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void editEmail() {
        userDto.setEmail("Update@mail.ru");
        userService.edit(1, userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id= :id", User.class);
        User user = query.setParameter("id", 1).getSingleResult();

        assertEquals(user.getId(), 1);
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}