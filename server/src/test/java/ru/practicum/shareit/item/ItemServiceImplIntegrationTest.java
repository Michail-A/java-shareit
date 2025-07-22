package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    private ItemDtoAdd itemDtoAdd;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder()
                .name("test")
                .email("Test@mail.ru")
                .build();
        itemDtoAdd = ItemDtoAdd.builder()
                .name("test")
                .description("test")
                .available(true)
                .build();
        userService.add(user);
        itemService.add(itemDtoAdd, 1);
    }

    @Test
    void get() {
        ItemDto getItem = itemService.get(1, 1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id= :id", Item.class);
        Item item = query.setParameter("id", 1).getSingleResult();

        assertEquals(getItem.getName(), item.getName());
        assertEquals(getItem.getDescription(), item.getDescription());
        assertEquals(getItem.getId(), item.getId());
    }
}