package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.RequestDtoItemGet;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRequestDto {
    private int id;
    private String description;
    private LocalDateTime created;
    private List<RequestDtoItemGet> items;
}
