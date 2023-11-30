package ru.practicum.shareit.booking.exception;

public class UnknowStateException extends IllegalStateException {
    public UnknowStateException(String message) {
        super(message);
    }
}
