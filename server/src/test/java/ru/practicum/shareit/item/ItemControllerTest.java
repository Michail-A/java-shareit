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
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.User;

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
    ItemDtoGet itemDtoGet;

    @BeforeEach
    void setUp() {
        itemDtoAdd = new ItemDtoAdd();
        itemDtoAdd.setName("Test");
        itemDtoAdd.setDescription("Test");
        itemDtoAdd.setAvailable(Boolean.TRUE);

        itemDtoGet = new ItemDtoGet();
        itemDtoGet.setId(1);
        itemDtoGet.setName(itemDtoAdd.getName());
        itemDtoGet.setDescription(itemDtoAdd.getDescription());
        itemDtoGet.setAvailable(itemDtoAdd.getAvailable());
    }

    @Test
    void create() throws Exception {
        when(itemService.create(any(ItemDtoAdd.class), anyInt())).thenReturn(itemDtoGet);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoGet.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoGet.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoGet.getDescription()));

        verify(itemService, times(1)).create(any(ItemDtoAdd.class), anyInt());
    }

    @Test
    void update() throws Exception {
        itemDtoAdd.setName("UpdateName");

        Item item = new Item();
        item.setId(itemDtoGet.getId());
        item.setOwner(new User());
        item.setName(itemDtoAdd.getName());
        item.setDescription(itemDtoGet.getDescription());
        item.setAvailable(itemDtoGet.getAvailable());


        when(itemService.update(any(ItemDtoAdd.class), anyInt(), anyInt())).thenReturn(ItemMapper.mapToGetItemDto(item));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDtoAdd))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(item.getId()))
                .andExpect(jsonPath("$.name").value(item.getName()))
                .andExpect(jsonPath("$.description").value(item.getDescription()));

        verify(itemService, times(1)).update(any(ItemDtoAdd.class), anyInt(), anyInt());
    }

    @Test
    void get() throws Exception {
        when(itemService.get(anyInt(), anyInt())).thenReturn(itemDtoGet);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoGet.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoGet.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoGet.getDescription()));

        verify(itemService, times(1)).get(anyInt(), anyInt());
    }

    @Test
    void getByUser() throws Exception {
        when(itemService.getByUser(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemDtoGet));

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemDtoGet.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDtoGet.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDtoGet.getDescription()));

        verify(itemService, times(1)).getByUser(anyInt(), anyInt(), anyInt());
    }

    @Test
    void search() throws Exception {
        Item item = new Item();
        item.setId(itemDtoGet.getId());
        item.setOwner(new User());
        item.setName(itemDtoGet.getName());
        item.setDescription(itemDtoGet.getDescription());
        item.setAvailable(itemDtoGet.getAvailable());
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(List.of(ItemMapper.mapToGetItemDto(item)));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/search?text=test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(item.getId()))
                .andExpect(jsonPath("$.[0].name").value(item.getName()))
                .andExpect(jsonPath("$.[0].description").value(item.getDescription()));

        verify(itemService, times(1)).search(anyString(), anyInt(), anyInt());
    }

    @Test
    void addComment() throws Exception {
        CommentDtoAdd commentDtoAdd = new CommentDtoAdd();
        commentDtoAdd.setText("Test");

        CommentDtoGet commentDtoGet = new CommentDtoGet();
        commentDtoGet.setId(1);
        commentDtoGet.setAuthorName("Test");
        commentDtoGet.setCreated(LocalDateTime.now());
        commentDtoGet.setText(commentDtoAdd.getText());

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