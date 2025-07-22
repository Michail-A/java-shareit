package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    private ItemDtoAdd itemDtoAdd;
    private User user;
    private User user2;
    private BookingDtoAdd bookingDtoAdd;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("test")
                .email("Test@mail.ru")
                .build();
        user2 = User.builder()
                .name("test")
                .email("Test2@mail.ru")
                .build();
        itemDtoAdd = ItemDtoAdd.builder()
                .name("test")
                .description("test")
                .available(true)
                .build();
        bookingDtoAdd = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
        userService.add(user);
        userService.add(user2);
        itemService.add(itemDtoAdd, 2);
        bookingService.add(bookingDtoAdd, 1);

    }

    @Test
    void setApprove() {
        bookingService.setApprove(2, 1, true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id= :id", Booking.class);
        Booking booking = query.setParameter("id", 1).getSingleResult();
        assertEquals(booking.getStatus(), Status.APPROVED);
    }

}