package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class User {

    private Integer id;

    private String name;

    @Email
    @NotBlank
    private String email;

}
