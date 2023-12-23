package ru.practicum.shareit.user.dto;

import com.sun.istack.NotNull;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class UserDto {
    private String name;

    private String email;
}
