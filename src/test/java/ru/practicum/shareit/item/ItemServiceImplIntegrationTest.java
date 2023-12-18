package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    private ItemDtoAdd itemDtoAdd;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        userDto = new UserDto("Test", "Test@mail.ru");
        itemDtoAdd = new ItemDtoAdd();
        itemDtoAdd.setName("Test");
        itemDtoAdd.setDescription("Test");
        itemDtoAdd.setAvailable(true);
        userService.create(userDto);
        itemService.create(itemDtoAdd, 1);
    }

    @Test
    void get() {
        ItemDtoGet getItem = itemService.get(1, 1);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id= :id", Item.class);
        Item item = query.setParameter("id", 1).getSingleResult();

        assertEquals(getItem.getName(), item.getName());
        assertEquals(getItem.getDescription(), item.getDescription());
        assertEquals(getItem.getId(), item.getId());
    }
}