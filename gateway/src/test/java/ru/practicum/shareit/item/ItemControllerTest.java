package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoAdd;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemClient client;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    ItemDtoAdd itemDtoAdd;

    @BeforeEach
    void setUp() {
        itemDtoAdd = new ItemDtoAdd();
        itemDtoAdd.setName("Test");
        itemDtoAdd.setDescription("Test");
        itemDtoAdd.setAvailable(Boolean.TRUE);
    }

    @Test
    void createItemFalseNotName() throws Exception {
        itemDtoAdd.setName(null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoAdd)))
                .andExpect(status().isBadRequest());
        verify(client, never()).create(any(ItemDtoAdd.class), anyInt());
    }

    @Test
    void createItemFalseNotDescription() throws Exception {
        itemDtoAdd.setDescription(null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoAdd)))
                .andExpect(status().isBadRequest());
        verify(client, never()).create(any(ItemDtoAdd.class), anyInt());
    }

    @Test
    void createItemFalseNotAvailable() throws Exception {
        itemDtoAdd.setAvailable(null);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoAdd)))
                .andExpect(status().isBadRequest());
        verify(client, never()).create(any(ItemDtoAdd.class), anyInt());
    }
}