package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDtoGet;

import java.util.List;

@Data
@Builder
public class ItemDtoOwners {

    private String name;

    private String description;

    private ItemBookingDtoGet lastBooking;

    private ItemBookingDtoGet nextBooking;

    private List<CommentDtoGet> comments;
}
