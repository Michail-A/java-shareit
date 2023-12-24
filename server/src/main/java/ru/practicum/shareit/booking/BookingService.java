package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;

import java.util.List;

public interface BookingService {
    Booking create(BookingDto bookingDto, int userId);

    BookingDtoGet setApprove(int userId, int bookingId, Boolean approve);

    BookingDtoGet getForOwnerOrBooker(int bookingId, int userId);

    List<BookingDtoGet> getForUser(String stateText, int userId, int from, int size);

    List<BookingDtoGet> getBookingsForOwner(String stateText, int userId, int from, int size);
}
