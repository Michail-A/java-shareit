package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.exception.UnknowStateException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State mapFromText(String stateText) {
        try {
            State state = null;
            state = State.valueOf(stateText.toUpperCase());
            return state;
        } catch (IllegalArgumentException e) {
            throw new UnknowStateException("Unknown state: " + stateText.toUpperCase());
        }
    }
}
