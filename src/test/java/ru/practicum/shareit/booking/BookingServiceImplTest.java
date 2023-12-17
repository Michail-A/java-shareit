package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.DateException;
import ru.practicum.shareit.error.ItemIsNotAvailableException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    User user;
    Item item;
    BookingDto bookingDto;
    Booking booking;
    Booking booking2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test");
        user.setEmail("test@test.com");

        item = new Item();
        item.setId(1);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(new User());

        bookingDto = new BookingDto(item.getId(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        booking2 = new Booking();
        booking2.setId(2);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now());
        booking2.setItem(item);
        booking2.setBooker(new User());
        booking2.setStatus(Status.APPROVED);
    }

    @Test
    void create() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        when(bookingRepository.findByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of(booking2));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking newBooking = bookingService.create(bookingDto, user.getId());

        assertEquals(newBooking, booking);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createFailStartDateAndEndDate() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().minusDays(1));

        final DateException e = assertThrows(DateException.class, () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));

        bookingDto.setEnd(bookingDto.getStart());
        final DateException b = assertThrows(DateException.class, () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createFailItemNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException b = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createNotAvailable() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        item.setAvailable(false);

        final ItemIsNotAvailableException b = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createFailMyItem() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        item.setOwner(user);

        final NotFoundException b = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createFailDateNotAvailable() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of(booking2));

        booking2.setStart(bookingDto.getEnd().minusDays(1));
        booking2.setEnd(bookingDto.getEnd().plusDays(1));
        final ItemIsNotAvailableException a = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));

        booking2.setStart(bookingDto.getStart().minusDays(1));
        booking2.setEnd(bookingDto.getStart().plusDays(1));
        final ItemIsNotAvailableException b = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));

        booking2.setStart(bookingDto.getStart().plusDays(1));
        booking2.setEnd(bookingDto.getEnd().minusDays(1));
        final ItemIsNotAvailableException c = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));

        booking2.setStart(bookingDto.getStart().minusDays(1));
        booking2.setEnd(bookingDto.getEnd().plusDays(1));
        final ItemIsNotAvailableException d = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.create(bookingDto, user.getId()));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void setApprove() {
        item.setOwner(user);
        booking2 = booking;
        Status status = Status.WAITING;
        booking.setStatus(status);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking2);

        BookingDtoGet bookingApproved = bookingService.setApprove(user.getId(), booking.getId(), Boolean.TRUE);
        verify(bookingRepository).save(any(Booking.class));
        assertEquals(bookingApproved, BookingMapper.mapToApprove(booking2));
    }

    @Test
    void setApproveFailBookingNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException a = assertThrows(NotFoundException.class,
                () -> bookingService.setApprove(user.getId(), booking.getId(), Boolean.TRUE));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void setApproveFailOwnerNotFound() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        final NotFoundException a = assertThrows(NotFoundException.class,
                () -> bookingService.setApprove(user.getId(), booking.getId(), Boolean.TRUE));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void setApproveFailAlreadyApprove() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));
        booking.setStatus(Status.APPROVED);
        item.setOwner(user);

        final ItemIsNotAvailableException a = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.setApprove(user.getId(), booking.getId(), Boolean.TRUE));
        verify(bookingRepository, never()).save(any(Booking.class));

        booking.setStatus(Status.REJECTED);
        final ItemIsNotAvailableException b = assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.setApprove(user.getId(), booking.getId(), Boolean.TRUE));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void getForOwnerOrBooker() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        BookingDtoGet bookingDtoGet = bookingService.getForOwnerOrBooker(booking.getId(), user.getId());

        assertEquals(bookingDtoGet, BookingMapper.mapToApprove(booking));
    }

    @Test
    void getForOwnerOrBookerFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getForOwnerOrBooker(booking.getId(), user.getId()));
        verify(bookingRepository, never()).findById(anyInt());
    }

    @Test
    void getForOwnerOrBookerFailBookingNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getForOwnerOrBooker(booking.getId(), user.getId()));
    }

    @Test
    void getForOwnerOrBookerFailBookingNotAvailable() {
        booking.setBooker(new User());
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.ofNullable(booking));

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getForOwnerOrBooker(booking.getId(), user.getId()));
    }

    @Test
    void getForUserFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getForUser("All", user.getId(), 0, 10));
    }

    @Test
    void getForUserFailPage() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));

        final DateException e = assertThrows(DateException.class,
                () -> bookingService.getForUser("All", user.getId(), -10, -20));
    }

    @Test
    void getBookingsForOwnerUserFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsForOwner("All", user.getId(), 0, 10));
    }

    @Test
    void getBookingsForOwnerFailPage() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));

        final DateException e = assertThrows(DateException.class,
                () -> bookingService.getBookingsForOwner("All", user.getId(), -10, -20));
    }
}