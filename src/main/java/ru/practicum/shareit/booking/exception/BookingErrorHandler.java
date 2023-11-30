package ru.practicum.shareit.booking.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class BookingErrorHandler {

    @ExceptionHandler({ItemIsNotAvailableException.class, DateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleItemIsNotAvailableException(final RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage(),
                "message: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundBookingException(final NotFoundBookingException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", "Бронирование не найден",
                "message: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUserNotFoundException(final UserNotFoundException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", "Ошибка пользователя",
                "message: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownStateException(final UnknowStateException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage());
    }
}

