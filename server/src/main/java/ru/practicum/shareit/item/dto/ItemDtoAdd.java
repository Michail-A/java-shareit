package ru.practicum.shareit.item.dto;


import lombok.Data;


@Data
public class ItemDtoAdd {
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
}
