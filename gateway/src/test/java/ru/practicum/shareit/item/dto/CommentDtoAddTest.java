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
class CommentDtoAddTest {
    private final JacksonTester<CommentDtoAdd> json;

    @Test
    void testSerialize() throws Exception {
        CommentDtoAdd dto = new CommentDtoAdd("Комментарий");
        String result = json.write(dto).getJson();
        assertTrue(result.contains("\"text\":\"Комментарий\""));
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"text\":\"Комментарий\"}";
        CommentDtoAdd dto = json.parseObject(content);
        assertEquals("Комментарий", dto.getText());
    }

    @Test
    void testValidationNullText() {
        CommentDtoAdd dto = new CommentDtoAdd();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CommentDtoAdd>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }

    @Test
    void testValidationBlankText() {
        CommentDtoAdd dto = new CommentDtoAdd("");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CommentDtoAdd>> violations = validator.validate(dto);
        assertEquals(1, violations.size());
    }
}