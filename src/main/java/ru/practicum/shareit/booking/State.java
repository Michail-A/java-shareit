package ru.practicum.shareit.booking;

import ru.practicum.shareit.error.UnknowStateException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State mapFromText(String stateText) {
        try {
            return State.valueOf(stateText.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnknowStateException("Unknown state: " + stateText.toUpperCase());
        }
    }
}
