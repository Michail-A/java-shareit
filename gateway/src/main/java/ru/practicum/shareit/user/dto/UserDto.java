package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;
}
