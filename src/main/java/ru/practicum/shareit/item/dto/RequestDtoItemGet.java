package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class RequestDtoItemGet {
    private int id;
    private String name;
    private String description;
    private int requestId;
    private Boolean available;
}
