package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class AddRequestDto {
    @NotBlank
    private String description;
}
