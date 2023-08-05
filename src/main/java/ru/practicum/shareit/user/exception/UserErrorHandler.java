package ru.practicum.shareit.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class UserErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserException(final UserException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", "Ошибка пользователя",
                "message: ", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailException(final EmailException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", "Такой email уже зарегистрирован",
                "message: ", e.getMessage());
    }
}
