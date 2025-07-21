package ru.practicum.shareit.item.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDtoGet {
    private int id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
