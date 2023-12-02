package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleErrorNotFound (final RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({ItemIsNotAvailableException.class, DateException.class,
            UnknowStateException.class, DateException.class, CommentAccessError.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleErrorBadRequest(final RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({EmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleErrorConflict(final RuntimeException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
