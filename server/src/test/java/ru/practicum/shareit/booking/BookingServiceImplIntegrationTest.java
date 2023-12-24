package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager em;
    private ItemDtoAdd itemDtoAdd;
    private UserDto userDto;
    private UserDto userDto2;
    private UserDto userDto3;
    private UserDto userDto4;
    private BookingDto bookingDto;

    private BookingDto bookingDtoByBooker;
    private BookingDto bookingDtoByOwner;
    private ItemDtoAdd itemDtoAdd2;
    private ItemDtoAdd itemDtoAdd3;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("Test", "Test@mail.ru");
        userDto2 = new UserDto("Test", "Test2@mail.ru");
        itemDtoAdd = new ItemDtoAdd();
        itemDtoAdd.setName("Test");
        itemDtoAdd.setDescription("Test");
        itemDtoAdd.setAvailable(true);
        bookingDto = new BookingDto(1, LocalDateTime.now(), LocalDateTime.now().plusDays(3));
        userService.create(userDto);
        userService.create(userDto2);
        itemService.create(itemDtoAdd, 2);
        bookingService.create(bookingDto, 1);

        itemDtoAdd2 = new ItemDtoAdd();
        itemDtoAdd2.setName("Test");
        itemDtoAdd2.setDescription("Test");
        itemDtoAdd2.setAvailable(true);
        itemService.create(itemDtoAdd2, 2);
        bookingDtoByBooker = new BookingDto(2, LocalDateTime.now(), LocalDateTime.now());
        userDto3 = new UserDto("GetBooker", "GetBooker@mail.ru");
        userDto4 = new UserDto("GetOwner", "GetOwner@mail.ru");
        userService.create(userDto3);
        userService.create(userDto4);
        itemDtoAdd2 = new ItemDtoAdd();
        itemDtoAdd2.setName("Test");
        itemDtoAdd2.setDescription("Test");
        itemDtoAdd2.setAvailable(true);
        itemService.create(itemDtoAdd2, 4);
        bookingDtoByOwner = new BookingDto(3, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void setApprove() {
        bookingService.setApprove(2, 1, true);

        TypedQuery<Booking> query = em.createQuery("Select b from Booking b where b.id= :id", Booking.class);
        Booking booking = query.setParameter("id", 1).getSingleResult();
        assertEquals(booking.getStatus(), Status.APPROVED);
    }

    @Test
    void getAllForUser() {
        List<BookingDtoGet> bookings = bookingService.getForUser("ALL", 1, 0, 10);
        TypedQuery<Booking> query = em
                .createQuery("Select b from Booking b where b.booker.id = :id order by b.id desc", Booking.class);
        Booking booking = query.setParameter("id", 1).getSingleResult();
        assertEquals(bookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getCurrentForUser() {
        bookingDtoByBooker.setStart(LocalDateTime.now().minusMinutes(15));
        bookingDtoByBooker.setEnd(LocalDateTime.now().plusMinutes(15));
        Booking booking = bookingService.create(bookingDtoByBooker, 3);

        List<BookingDtoGet> getBookings = bookingService.getForUser("Current", 3, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getPastForUser() {
        bookingDtoByBooker.setStart(LocalDateTime.now().minusMinutes(30));
        bookingDtoByBooker.setEnd(LocalDateTime.now().minusMinutes(15));
        Booking booking = bookingService.create(bookingDtoByBooker, 3);

        List<BookingDtoGet> getBookings = bookingService.getForUser("Past", 3, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getFutureForUser() {
        bookingDtoByBooker.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByBooker.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByBooker, 3);

        List<BookingDtoGet> getBookings = bookingService.getForUser("Future", 3, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getWaitingForUser() {
        bookingDtoByBooker.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByBooker.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByBooker, 3);


        List<BookingDtoGet> getBookings = bookingService.getForUser("Waiting", 3, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getRejectedForUser() {
        bookingDtoByBooker.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByBooker.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByBooker, 3);
        bookingService.setApprove(2, booking.getId(), false);

        List<BookingDtoGet> getBookings = bookingService.getForUser("REJECTED", 3, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getAllForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().minusMinutes(15));
        bookingDtoByOwner.setEnd(LocalDateTime.now().plusMinutes(15));
        bookingService.create(bookingDtoByOwner, 3);

        List<BookingDtoGet> bookings = bookingService.getBookingsForOwner("ALL", 4, 0, 10);
        TypedQuery<Booking> query = em
                .createQuery("select b from Booking b where b.item.owner.id = :id order by b.start desc", Booking.class);
        Booking booking = query.setParameter("id", 4).getSingleResult();
        assertEquals(bookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getCurrentForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().minusMinutes(15));
        bookingDtoByOwner.setEnd(LocalDateTime.now().plusMinutes(15));
        Booking booking = bookingService.create(bookingDtoByOwner, 3);

        List<BookingDtoGet> getBookings = bookingService.getBookingsForOwner("Current", 4, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getPastForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().minusMinutes(30));
        bookingDtoByOwner.setEnd(LocalDateTime.now().minusMinutes(15));
        Booking booking = bookingService.create(bookingDtoByOwner, 3);

        List<BookingDtoGet> getBookings = bookingService.getBookingsForOwner("Past", 4, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getFutureForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByOwner.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByOwner, 3);

        List<BookingDtoGet> getBookings = bookingService.getBookingsForOwner("Future", 4, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getWaitingForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByOwner.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByOwner, 3);


        List<BookingDtoGet> getBookings = bookingService.getBookingsForOwner("Waiting", 4, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }

    @Test
    void getRejectedForOwner() {
        bookingDtoByOwner.setStart(LocalDateTime.now().plusMinutes(30));
        bookingDtoByOwner.setEnd(LocalDateTime.now().plusMinutes(45));
        Booking booking = bookingService.create(bookingDtoByOwner, 3);
        bookingService.setApprove(4, booking.getId(), false);

        List<BookingDtoGet> getBookings = bookingService.getBookingsForOwner("REJECTED", 4, 0, 10);
        assertEquals(getBookings.get(0), BookingMapper.mapToApprove(booking));
    }
}