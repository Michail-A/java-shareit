package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRequestDtoItemGet {
    private int id;
    private String name;
    private String description;
    private int requestId;
    private Boolean available;
}
