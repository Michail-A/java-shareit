package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

public class BookingMapper {
    public static Booking mapToNewBooking(BookingDto bookingDto, User user, Item item){
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(Status.WAITING);
        booking.setItem(item);
        return booking;
    }

    public static BookingDtoGet mapToApprove(Booking booking){
        BookingDtoGet bookingDtoForApprove = new BookingDtoGet();
        bookingDtoForApprove.setId(booking.getId());
        bookingDtoForApprove.setStart(booking.getStart());
        bookingDtoForApprove.setEnd(booking.getEnd());
        bookingDtoForApprove.setStatus(booking.getStatus());
        bookingDtoForApprove.setBooker(UserMapper.mapToBookingUserDto(booking.getBooker()));
        bookingDtoForApprove.setItem(ItemMapper.mapToBookingItemDto(booking.getItem()));
        return bookingDtoForApprove;
    }

    public static ItemBookingDtoGet mapToItemBookingDtoGet(Booking booking){
        ItemBookingDtoGet itemBookingDtoGet = new ItemBookingDtoGet();
        if(booking != null){
            itemBookingDtoGet.setId(booking.getId());
            itemBookingDtoGet.setBookerId(booking.getBooker().getId());
        }
        return itemBookingDtoGet;
    }
}
