package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Builder
@Data
public class ItemDto {

    private Integer id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;
}
