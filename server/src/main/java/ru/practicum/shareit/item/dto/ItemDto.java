package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Builder
@Data
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemBookingDtoGet lastBooking;

    private ItemBookingDtoGet nextBooking;

    private List<CommentDtoGet> comments;
}
