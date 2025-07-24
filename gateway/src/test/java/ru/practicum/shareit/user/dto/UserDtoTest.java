package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {
    private final JacksonTester<UserDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("ivan@mail.ru");
        String result = json.write(dto).getJson();
        assertTrue(result.contains("\"name\":\"Иван\""));
        assertTrue(result.contains("\"email\":\"ivan@mail.ru\""));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"Иван\",\"email\":\"ivan@mail.ru\"}";
        UserDto dto = json.parseObject(content);
        assertEquals("Иван", dto.getName());
        assertEquals("ivan@mail.ru", dto.getEmail());
    }

    @Test
    void testValidationNullFields() {
        UserDto dto = new UserDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertEquals(2, violations.size());
    }

    @Test
    void testValidationInvalidEmail() {
        UserDto dto = new UserDto();
        dto.setName("Иван");
        dto.setEmail("not-an-email");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<UserDto>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }
}