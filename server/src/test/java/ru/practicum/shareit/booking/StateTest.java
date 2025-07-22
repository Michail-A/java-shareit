package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.State;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StateTest {
    @Test
    void mapFromTextShouldReturnAllStates() {
        assertEquals(State.ALL, State.mapFromText("ALL"));
        assertEquals(State.CURRENT, State.mapFromText("CURRENT"));
        assertEquals(State.PAST, State.mapFromText("PAST"));
        assertEquals(State.FUTURE, State.mapFromText("FUTURE"));
        assertEquals(State.WAITING, State.mapFromText("WAITING"));
        assertEquals(State.REJECTED, State.mapFromText("REJECTED"));
    }

    @Test
    void mapFromTextShouldBeCaseInsensitive() {
        assertEquals(State.ALL, State.mapFromText("all"));
        assertEquals(State.CURRENT, State.mapFromText("current"));
        assertEquals(State.PAST, State.mapFromText("past"));
        assertEquals(State.FUTURE, State.mapFromText("future"));
        assertEquals(State.WAITING, State.mapFromText("waiting"));
        assertEquals(State.REJECTED, State.mapFromText("rejected"));
    }

    @Test
    void mapFromTextShouldThrowExceptionForUnknownState() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> State.mapFromText("UNKNOWN"));
        assertEquals("Unknown state: UNKNOWN", exception.getMessage());
    }
}