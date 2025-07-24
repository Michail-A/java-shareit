package ru.practicum.shareit.item.dto;

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
class ItemDtoAddTest {
    private final JacksonTester<ItemDtoAdd> json;

    @Test
    void testSerialize() throws Exception {
        ItemDtoAdd dto = new ItemDtoAdd();
        dto.setName("Вещь");
        dto.setDescription("Описание");
        dto.setAvailable(true);
        dto.setRequestId(123);
        String result = json.write(dto).getJson();
        assertTrue(result.contains("\"name\":\"Вещь\""));
        assertTrue(result.contains("\"description\":\"Описание\""));
        assertTrue(result.contains("\"available\":true"));
        assertTrue(result.contains("\"requestId\":123"));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"Вещь\",\"description\":\"Описание\",\"available\":true,\"requestId\":123}";
        ItemDtoAdd dto = json.parseObject(content);
        assertEquals("Вещь", dto.getName());
        assertEquals("Описание", dto.getDescription());
        assertEquals(true, dto.getAvailable());
        assertEquals(123, dto.getRequestId());
    }

    @Test
    void testValidationNullFields() {
        ItemDtoAdd dto = new ItemDtoAdd();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ItemDtoAdd>> violations = validator.validate(dto);
        assertEquals(3, violations.size());
    }

    @Test
    void testValidationBlankName() {
        ItemDtoAdd dto = new ItemDtoAdd();
        dto.setName("");
        dto.setDescription("Описание");
        dto.setAvailable(true);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ItemDtoAdd>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    void testValidationBlankDescription() {
        ItemDtoAdd dto = new ItemDtoAdd();
        dto.setName("Вещь");
        dto.setDescription("");
        dto.setAvailable(true);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ItemDtoAdd>> violations = validator.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }
}