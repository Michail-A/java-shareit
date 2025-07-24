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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.CommentAccessException;
import ru.practicum.shareit.error.NotAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    private final BookingService bookingService;
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

    @Test
    void getShouldReturnBookingsAndCommentsMapped() {
        User user2 = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();
        userService.add(user2);
        BookingDtoAdd bookingDtoAdd = BookingDtoAdd.builder()
                .itemId(1)
                .start(java.time.LocalDateTime.now().minusDays(2))
                .end(java.time.LocalDateTime.now().minusDays(1))
                .build();
        BookingDto booking = bookingService.add(bookingDtoAdd, 2);
        bookingService.setApprove(1, booking.getId(), true);
        CommentDtoAdd commentDtoAdd = CommentDtoAdd.builder()
                .text("Комментарий к вещи")
                .build();
        itemService.addComment(1, 2, commentDtoAdd);

        ItemDto getItem = itemService.get(1, 1);

        assertThat(getItem.getLastBooking()).isNotNull();
        assertThat(getItem.getLastBooking().getId()).isEqualTo(booking.getId());
        assertThat(getItem.getNextBooking()).isNull();
        assertThat(getItem.getComments()).isNotEmpty();
        assertThat(getItem.getComments().get(0).getText()).isEqualTo("Комментарий к вещи");
    }

    @Test
    void shouldThrowCommentAccessException() {
        CommentDtoAdd commentDtoAdd = CommentDtoAdd.builder()
                .text("Тестовый комментарий")
                .build();
        // Пользователь с id=2 не брал вещь в аренду
        User anotherUser = User.builder()
                .name("Другой пользователь")
                .email("other@mail.ru")
                .build();
        userService.add(anotherUser);
        Exception exception = assertThrows(
                CommentAccessException.class,
                () -> itemService.addComment(1, 2, commentDtoAdd)
        );
        assertEquals("Пользователь 2не брал вещь 1 в аренду", exception.getMessage());
    }

    @Test
    void updateItem() {
        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("updated");
        ItemDto updated = itemService.update(updateDto, 1, 1);
        assertEquals("updated", updated.getName());
        assertEquals("test", updated.getDescription());
        assertEquals(true, updated.getAvailable());
    }

    @Test
    void getByOwner() {
        ItemDtoAdd itemDtoAdd2 = ItemDtoAdd.builder()
                .name("item2")
                .description("desc2")
                .available(true)
                .build();
        itemService.add(itemDtoAdd2, 1);
        List<ItemDtoOwners> items = itemService.getByOwner(1);
        assertEquals(2, items.size());
        assertEquals("test", items.get(0).getName());
        assertEquals("item2", items.get(1).getName());
    }

    @Test
    void searchItems() {

        List<ItemDto> found = itemService.search("test");
        assertEquals(1, found.size());
        assertEquals("test", found.get(0).getName());
        // Поиск по несуществующему
        List<ItemDto> notFound = itemService.search("notfound");
        assertEquals(0, notFound.size());
    }


    @Test
    void updateAllFields() {
        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("newName");
        updateDto.setDescription("newDesc");
        updateDto.setAvailable(false);
        ItemDto updated = itemService.update(updateDto, 1, 1);
        assertEquals("newName", updated.getName());
        assertEquals("newDesc", updated.getDescription());
        assertEquals(false, updated.getAvailable());
    }

    @Test
    void updateNotOwnerThrows() {
        User anotherUser = User.builder().name("notOwner").email("notowner@mail.ru").build();
        userService.add(anotherUser);
        UpdateItemDto updateDto = new UpdateItemDto();
        updateDto.setName("fail");
        assertThrows(
                NotAccessException.class,
                () -> itemService.update(updateDto, 2, 1)
        );
    }

    @Test
    void getByOwnerWithCommentsAndBookings() {
        User user2 = User.builder().name("user2").email("user2@mail.ru").build();
        userService.add(user2);
        Item item = em.find(Item.class, 1);
        Comment comment = Comment.builder()
                .text("Комментарий")
                .item(item)
                .author(em.find(User.class, 1))
                .created(java.time.LocalDateTime.now())
                .build();
        em.persist(comment);
        em.flush();
        List<ItemDtoOwners> items = itemService.getByOwner(1);
        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getComments().size());
        assertEquals("Комментарий", items.get(0).getComments().get(0).getText());
    }

    @Test
    void searchBlankReturnsEmpty() {
        List<ItemDto> found = itemService.search("");
        assertEquals(0, found.size());
    }

    @Test
    void searchUnavailableFiltered() {
        ItemDtoAdd unavailable = ItemDtoAdd.builder()
                .name("unavailable")
                .description("desc")
                .available(false)
                .build();
        itemService.add(unavailable, 1);
        List<ItemDto> found = itemService.search("unavailable");
        assertEquals(0, found.size());
    }

    @Test
    void addCommentShouldThrowCommentAccessExceptionIfUserNotBooked() {
        // Пользователь с id=2 не арендовал вещь
        User anotherUser = User.builder()
                .name("Другой пользователь")
                .email("other@mail.ru")
                .build();
        userService.add(anotherUser);
        CommentDtoAdd commentDtoAdd = CommentDtoAdd.builder()
                .text("Комментарий без аренды")
                .build();
        Exception exception = assertThrows(
                CommentAccessException.class,
                () -> itemService.addComment(1, 2, commentDtoAdd)
        );
        assertEquals("Пользователь 2не брал вещь 1 в аренду", exception.getMessage());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionIfItemNotFound() {
        CommentDtoAdd commentDtoAdd = CommentDtoAdd.builder()
                .text("Комментарий к несуществующей вещи")
                .build();
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(999, 1, commentDtoAdd)
        );
        assertEquals("Предмет не найден", exception.getMessage());
    }

    @Test
    void addCommentShouldThrowNotFoundExceptionIfUserNotFound() {
        CommentDtoAdd commentDtoAdd = CommentDtoAdd.builder()
                .text("Комментарий от несуществующего пользователя")
                .build();
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(1, 999, commentDtoAdd)
        );
        assertEquals("Пользователь id= 999 не найден.", exception.getMessage());
    }
}