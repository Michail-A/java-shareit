package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ItemBookingDtoGet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoGet {
    private int id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingDtoGet lastBooking;
    private ItemBookingDtoGet nextBooking;
}
