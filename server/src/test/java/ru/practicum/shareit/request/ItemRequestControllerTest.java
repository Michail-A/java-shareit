package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    AddItemRequestDto addItemRequestDto;
    GetItemRequestDto getItemRequestDto;

    @BeforeEach
    void setUp() {
        addItemRequestDto = AddItemRequestDto.builder()
                .description("desc")
                .build();
        getItemRequestDto = GetItemRequestDto.builder()
                .id(1)
                .description("desc")
                .build();
    }

    @Test
    void createRequest() throws Exception {
        when(itemRequestService.addRequest(any(AddItemRequestDto.class), anyInt())).thenReturn(getItemRequestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).addRequest(any(AddItemRequestDto.class), anyInt());
    }

    @Test
    void getOwnRequests() throws Exception {
        when(itemRequestService.getRequestsByOwner(anyInt())).thenReturn(List.of(getItemRequestDto));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).getRequestsByOwner(anyInt());
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyInt())).thenReturn(List.of(getItemRequestDto));
        mockMvc.perform(get("/requests/all?from=0&size=10")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).getAllRequests(anyInt());
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(getItemRequestDto);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).getRequestById(anyInt(), anyInt());
    }
} 