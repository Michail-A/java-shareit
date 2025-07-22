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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.DateException;
import ru.practicum.shareit.error.ItemIsNotAvailableException;
import ru.practicum.shareit.error.NotAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void addShouldThrowDateExceptionWhenEndBeforeStart() {
        BookingDtoAdd invalidBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(1))
                .build();
        Exception exception = assertThrows(
                DateException.class,
                () -> bookingService.add(invalidBooking, 1)
        );
        assertEquals("Дата окончания не может быть раньше или равна дате начала", exception.getMessage());
    }

    @Test
    void addShouldThrowItemIsNotAvailableException() {
        ItemDtoAdd unavailableItem = ItemDtoAdd.builder()
                .name("unavailable")
                .description("no")
                .available(false)
                .build();
        itemService.add(unavailableItem, 2);
        BookingDtoAdd booking = BookingDtoAdd.builder()
                .itemId(2)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        Exception exception = assertThrows(
                ItemIsNotAvailableException.class,
                () -> bookingService.add(booking, 1)
        );
        assertEquals("Вещь не доступна для бронирования", exception.getMessage());
    }

    @Test
    void shouldThrowNotAccessExceptionWhenUserNotOwnerApprove() {

        Exception exception = assertThrows(
                NotAccessException.class,
                () -> bookingService.setApprove(1, 1, true)
        );
        assertThat(exception.getMessage()).contains("Бронирование пользователя id = ");
    }

    @Test
    void getForUserAll() {

        List<BookingDto> bookings = bookingService.getForUser(1, "ALL");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(1);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(1);
    }

    @Test
    void getForOwnerAll() {

        List<BookingDto> bookings = bookingService.getForOwner(2, "ALL");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getForUserCurrent() {

        BookingDtoAdd currentBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();
        bookingService.add(currentBooking, 1);
        bookingService.setApprove(2, 2, true); // подтверждаем второе бронирование

        List<BookingDto> bookings = bookingService.getForUser(1, "CURRENT");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getForUserPast() {
        BookingDtoAdd pastBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();
        BookingDto created = bookingService.add(pastBooking, 1);
        bookingService.setApprove(2, created.getId(), true);

        List<BookingDto> bookings = bookingService.getForUser(1, "PAST");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getForUserFuture() {

        List<BookingDto> bookings = bookingService.getForUser(1, "FUTURE");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getBooker().getId()).isEqualTo(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getForUserWaiting() {

        List<BookingDto> bookings = bookingService.getForUser(1, "WAITING");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getForUserRejected() {
        BookingDtoAdd rejectedBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .build();
        BookingDto created = bookingService.add(rejectedBooking, 1);
        bookingService.setApprove(2, created.getId(), false);

        List<BookingDto> bookings = bookingService.getForUser(1, "REJECTED");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void getForOwnerCurrent() {
        BookingDtoAdd currentBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().plusHours(1))
                .build();
        bookingService.add(currentBooking, 1);
        bookingService.setApprove(2, 2, true);

        List<BookingDto> bookings = bookingService.getForOwner(2, "CURRENT");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getForOwnerPast() {
        BookingDtoAdd pastBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(2))
                .build();
        BookingDto created = bookingService.add(pastBooking, 1);
        bookingService.setApprove(2, created.getId(), true);

        List<BookingDto> bookings = bookingService.getForOwner(2, "PAST");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void getForOwnerFuture() {
        List<BookingDto> bookings = bookingService.getForOwner(2, "FUTURE");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getForOwnerWaiting() {
        List<BookingDto> bookings = bookingService.getForOwner(2, "WAITING");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.WAITING);
    }

    @Test
    void getForOwnerRejected() {
        BookingDtoAdd rejectedBooking = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .build();
        BookingDto created = bookingService.add(rejectedBooking, 1);
        bookingService.setApprove(2, created.getId(), false);

        List<BookingDto> bookings = bookingService.getForOwner(2, "REJECTED");
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void setApproveShouldThrowNotAccessExceptionWhenUserNotOwner() {
        Exception exception = assertThrows(
                NotAccessException.class,
                () -> bookingService.setApprove(1, 1, true)
        );
        assertThat(exception.getMessage()).contains("Бронирование пользователя id = ");
    }

    @Test
    void getShouldThrowNotFoundExceptionForWrongUser() {
        User user3 = User.builder().name("user3").email("user3@mail.ru").build();
        userService.add(user3);
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.get(3, 1)
        );
        assertThat(exception.getMessage()).contains("Ошибка доступа");
    }
}