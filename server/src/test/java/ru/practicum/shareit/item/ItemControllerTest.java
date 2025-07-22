package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    ItemDtoAdd itemDtoAdd;
    ItemDto itemDto;
    CommentDtoAdd commentDtoAdd;
    CommentDtoGet commentDtoGet;

    @BeforeEach
    void setUp() {
        itemDtoAdd = ItemDtoAdd.builder()
                .name("item")
                .description("desc")
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .id(1)
                .name("item")
                .description("desc")
                .available(true)
                .build();
        commentDtoAdd = CommentDtoAdd.builder()
                .text("comment")
                .build();
        commentDtoGet = CommentDtoGet.builder()
                .id(1)
                .text("comment")
                .authorName("user")
                .build();
    }

    @Test
    void createItem() throws Exception {
        when(itemService.add(any(ItemDtoAdd.class), anyInt())).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).add(any(ItemDtoAdd.class), anyInt());
    }

    @Test
    void getItem() throws Exception {
        when(itemService.get(anyInt(), anyInt())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).get(anyInt(), anyInt());
    }

    @Test
    void updateItem() throws Exception {
        when(itemService.update(any(UpdateItemDto.class), anyInt(), anyInt())).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
        verify(itemService, times(1)).update(any(UpdateItemDto.class), anyInt(), anyInt());
    }

    @Test
    void searchItems() throws Exception {
        when(itemService.search(anyString())).thenReturn(List.of(itemDto));
        mockMvc.perform(get("/items/search?text=item")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()));
        verify(itemService, times(1)).search(anyString());
    }

    @Test
    void addComment() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDtoAdd.class))).thenReturn(commentDtoGet);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDtoAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDtoGet.getId()));
        verify(itemService, times(1)).addComment(anyInt(), anyInt(), any(CommentDtoAdd.class));
    }

    @Test
    void getByOwner() throws Exception {
        ItemBookingDtoGet bookingDtoGet = ItemBookingDtoGet.builder()
                .id(1)
                .bookerId(1)
                .time(java.time.LocalDateTime.now())
                .build();
        ItemDtoOwners owners = ItemDtoOwners.builder()
                .name("item")
                .description("desc")
                .lastBooking(bookingDtoGet)
                .nextBooking(null)
                .comments(List.of())
                .build();
        when(itemService.getByOwner(anyInt())).thenReturn(List.of(owners));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].name").value("item"));
        verify(itemService, times(1)).getByOwner(anyInt());
    }
}