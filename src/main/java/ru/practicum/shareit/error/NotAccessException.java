package ru.practicum.shareit.error;

public class NotAccessException extends RuntimeException {
    public NotAccessException(String message) {
        super(message);
    }
}
