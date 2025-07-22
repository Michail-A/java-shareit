package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class RequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    AddItemRequestDto addItemRequestDto;
    GetItemRequestDto getItemRequestDto;

    @BeforeEach
    void setUp() {
        addItemRequestDto = new AddItemRequestDto();
        addItemRequestDto.setDescription("Test");

        getItemRequestDto = new GetItemRequestDto();
        getItemRequestDto.setId(1);
        getItemRequestDto.setItems(List.of());
        getItemRequestDto.setCreated(LocalDateTime.now());
        getItemRequestDto.setDescription(addItemRequestDto.getDescription());
    }

    @Test
    void addRequest() throws Exception {
        when(itemRequestService.addRequest(ArgumentMatchers.any(AddItemRequestDto.class),
                anyInt())).thenReturn(getItemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(addItemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()));

        verify(itemRequestService, times(1)).addRequest(addItemRequestDto, 1);
    }

    @Test
    void getRequestsByOwner() throws Exception {
        when(itemRequestService.getRequestsByOwner(anyInt())).thenReturn(List.of(getItemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).getRequestsByOwner(1);
    }

    @Test
    void getAllRequests() throws Exception {
        when(itemRequestService.getAllRequests(anyInt()))
                .thenReturn(List.of(getItemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getItemRequestDto.getId()));
        verify(itemRequestService, times(1)).getAllRequests(anyInt());
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(getItemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getItemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(getItemRequestDto.getDescription()));
        verify(itemRequestService, times(1)).getRequestById(anyInt(), anyInt());
    }
}