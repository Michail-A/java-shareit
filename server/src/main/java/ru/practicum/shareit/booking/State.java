package ru.practicum.shareit.booking;

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
            throw new IllegalArgumentException("Unknown state: " + stateText.toUpperCase());
        }
    }
}
