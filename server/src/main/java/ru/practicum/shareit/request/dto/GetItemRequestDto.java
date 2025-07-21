package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemRequestDtoItemGet;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetItemRequestDto {
    private int id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestDtoItemGet> items;
}
