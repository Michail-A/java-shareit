package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemBookingDtoGet {
    private int id;
    private int bookerId;
    private LocalDateTime time;
}