package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.user.dto.BookingUserDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoGet {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private BookingUserDto booker;
    private BookingItemDto item;
}
