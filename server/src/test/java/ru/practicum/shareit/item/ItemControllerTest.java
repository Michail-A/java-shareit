package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    ItemDtoAdd itemDtoAdd;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDtoAdd = ItemDtoAdd.builder()
                .name("test")
                .description("test")
                .available(Boolean.TRUE)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .name(itemDtoAdd.getName())
                .description(itemDtoAdd.getDescription())
                .available(itemDtoAdd.getAvailable())
                .build();
    }

    @Test
    void create() throws Exception {
        when(itemService.add(any(ItemDtoAdd.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).add(any(ItemDtoAdd.class), anyInt());
    }

    @Test
    void update() throws Exception {

        when(itemService.update(any(UpdateItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).update(any(UpdateItemDto.class), anyInt(), anyInt());
    }

    @Test
    void get() throws Exception {
        when(itemService.get(anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));

        verify(itemService, times(1)).get(anyInt(), anyInt());
    }

    @Test
    void getByOwner() throws Exception {
        ItemDtoOwners itemDtoOwners = ItemDtoOwners.builder()
                .name("test")
                .build();

        when(itemService.getByOwner(anyInt())).thenReturn(List.of(itemDtoOwners));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value(itemDtoOwners.getName()));

        verify(itemService, times(1)).getByOwner(anyInt());
    }

    @Test
    void search() throws Exception {

        when(itemService.search(anyString())).thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()));

        verify(itemService, times(1)).search(anyString());
    }

    @Test
    void addComment() throws Exception {
        CommentDtoAdd commentDtoAdd = new CommentDtoAdd();
        commentDtoAdd.setText("Test");

        CommentDtoGet commentDtoGet = CommentDtoGet.builder()
                .id(1)
                .authorName("t")
                .created(LocalDateTime.now())
                .text(commentDtoAdd.getText())
                .build();

        when(itemService.addComment(anyInt(), anyInt(), any(CommentDtoAdd.class))).thenReturn(commentDtoGet);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDtoAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoGet.getId()))
                .andExpect(jsonPath("$.authorName").value(commentDtoGet.getAuthorName()))
                .andExpect(jsonPath("$.text").value(commentDtoGet.getText()));

        verify(itemService, times(1)).addComment(anyInt(), anyInt(), any(CommentDtoAdd.class));
    }
}