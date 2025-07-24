package ru.practicum.shareit.request.dto;

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
class AddRequestDtoTest {
    private final JacksonTester<AddRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        AddRequestDto dto = new AddRequestDto();
        dto.setDescription("Описание запроса");
        String result = json.write(dto).getJson();
        assertTrue(result.contains("\"description\":\"Описание запроса\""));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"description\":\"Описание запроса\"}";
        AddRequestDto dto = json.parseObject(content);
        assertEquals("Описание запроса", dto.getDescription());
    }

    @Test
    void testValidationNullDescription() {
        AddRequestDto dto = new AddRequestDto();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AddRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    void testValidationBlankDescription() {
        AddRequestDto dto = new AddRequestDto();
        dto.setDescription("");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<AddRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}