package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.of(2024, 1, 1, 12, 0), LocalDateTime.of(2024, 1, 2, 12, 0));
        String result = json.write(bookingDto).getJson();
        assertTrue(result.contains("\"itemId\":1"));
        assertTrue(result.contains("2024-01-01T12:00:00"));
        assertTrue(result.contains("2024-01-02T12:00:00"));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{" +
                "\"itemId\":1," +
                "\"start\":\"2024-01-01T12:00:00\"," +
                "\"end\":\"2024-01-02T12:00:00\"}";
        BookingDto bookingDto = json.parseObject(content);
        assertEquals(1, bookingDto.getItemId());
        assertEquals(LocalDateTime.of(2024, 1, 1, 12, 0), bookingDto.getStart());
        assertEquals(LocalDateTime.of(2024, 1, 2, 12, 0), bookingDto.getEnd());
    }

    @Test
    void testValidationNullFields() {
        BookingDto bookingDto = new BookingDto(null, null, null);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertEquals(3, violations.size());
    }

    @Test
    void testValidationPastStart() {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("start")));
    }

    @Test
    void testValidationEndNotFuture() {
        BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().minusDays(1));
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("end")));
    }
}