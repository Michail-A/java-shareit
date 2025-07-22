package ru.practicum.shareit.error;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ErrorHandlerTest {
    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundException() {
        NotFoundException ex = new NotFoundException("not found");
        Map<String, String> response = errorHandler.handleConflict(ex);
        assertThat(response).containsEntry("error", "not found");
    }

    @Test
    void handleEmailAlreadyExists() {
        EmailAlreadyExists ex = new EmailAlreadyExists("email exists");
        Map<String, String> response = errorHandler.handleConflict(ex);
        assertThat(response).containsEntry("error", "email exists");
    }

    @Test
    void handleBadRequest_NotAccessException() {
        NotAccessException ex = new NotAccessException("no access");
        Map<String, String> response = errorHandler.handleBadRequest(ex);
        assertThat(response).containsEntry("error", "no access");
    }

    @Test
    void handleBadRequest_DateException() {
        DateException ex = new DateException("bad date");
        Map<String, String> response = errorHandler.handleBadRequest(ex);
        assertThat(response).containsEntry("error", "bad date");
    }

    @Test
    void handleBadRequest_ItemIsNotAvailableException() {
        ItemIsNotAvailableException ex = new ItemIsNotAvailableException("not available");
        Map<String, String> response = errorHandler.handleBadRequest(ex);
        assertThat(response).containsEntry("error", "not available");
    }

    @Test
    void handleBadRequest_CommentAccessException() {
        CommentAccessException ex = new CommentAccessException("no comment access");
        Map<String, String> response = errorHandler.handleBadRequest(ex);
        assertThat(response).containsEntry("error", "no comment access");
    }

    @Test
    void handleBadRequest_MethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getMessage()).thenReturn("validation error");
        Map<String, String> response = errorHandler.handleBadRequest(ex);
        assertThat(response).containsEntry("error", "validation error");
    }
} 