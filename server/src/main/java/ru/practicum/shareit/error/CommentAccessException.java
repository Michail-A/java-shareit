package ru.practicum.shareit.error;

public class CommentAccessException extends RuntimeException {
    public CommentAccessException(String message) {
        super(message);
    }
}
