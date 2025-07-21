package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;

import java.util.List;

public interface BookingService {

    BookingDto add(BookingDtoAdd bookingDtoAdd, int bookerId);

    BookingDto setApprove(int userId, int bookingId, Boolean approve);

    BookingDto get(int userId, int bookingId);

    List<BookingDto> getForUser(int userId, String stateText);

    List<BookingDto> getForOwner(int userId, String stateText);
}
