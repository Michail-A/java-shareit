package ru.practicum.shareit.error;

public class CommentAccessError extends RuntimeException {
    public CommentAccessError(String message) {
        super(message);
    }
}
