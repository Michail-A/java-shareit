package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toNewBooking(BookingDtoAdd bookingDtoAdd, Item item, User user) {
        return Booking.builder()
                .start(bookingDtoAdd.getStart())
                .end(bookingDtoAdd.getEnd())
                .item(item)
                .booker(user)
                .status(Status.WAITING)
                .build();
    }
}
