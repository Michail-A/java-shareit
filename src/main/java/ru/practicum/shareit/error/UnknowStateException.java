package ru.practicum.shareit.error;

public class UnknowStateException extends IllegalStateException {
    public UnknowStateException(String message) {
        super(message);
    }
}
