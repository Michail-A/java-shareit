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
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @MockBean
    RequestService requestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    AddRequestDto addRequestDto;
    GetRequestDto getRequestDto;

    @BeforeEach
    void setUp() {
        addRequestDto = new AddRequestDto();
        addRequestDto.setDescription("Test");

        getRequestDto = new GetRequestDto();
        getRequestDto.setId(1);
        getRequestDto.setItems(List.of());
        getRequestDto.setCreated(LocalDateTime.now());
        getRequestDto.setDescription(addRequestDto.getDescription());
    }

    @Test
    void addRequest() throws Exception {
        when(requestService.addRequest(ArgumentMatchers.any(AddRequestDto.class),
                anyInt())).thenReturn(getRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(addRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getRequestDto.getId()));

        verify(requestService, times(1)).addRequest(addRequestDto, 1);
    }

    @Test
    void getRequestsByOwner() throws Exception {
        when(requestService.getRequestsByOwner(anyInt())).thenReturn(List.of(getRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getRequestDto.getId()));
        verify(requestService, times(1)).getRequestsByOwner(1);
    }

    @Test
    void getAllRequests() throws Exception {
        when(requestService.getAllRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(getRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(getRequestDto.getId()));
        verify(requestService, times(1)).getAllRequests(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getRequestById() throws Exception {
        when(requestService.getRequestById(anyInt(), anyInt())).thenReturn(getRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(getRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(getRequestDto.getDescription()));
        verify(requestService, times(1)).getRequestById(anyInt(), anyInt());
    }
}