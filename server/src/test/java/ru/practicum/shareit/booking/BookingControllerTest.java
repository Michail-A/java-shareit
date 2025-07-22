package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    BookingDtoAdd bookingDtoAdd;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDtoAdd = BookingDtoAdd.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();
        bookingDto = BookingDto.builder()
                .id(1)
                .start(bookingDtoAdd.getStart())
                .end(bookingDtoAdd.getEnd())
                .status(Status.WAITING)
                .booker(new User())
                .item(new Item())
                .build();
    }

    @Test
    void createBooking() throws Exception {

        when(bookingService.add(any(BookingDtoAdd.class), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(objectMapper.writeValueAsString(bookingDtoAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));


        verify(bookingService, times(1)).add(any(BookingDtoAdd.class), anyInt());
    }

    @Test
    void setApprove() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        when(bookingService.setApprove(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));

        verify(bookingService, times(1)).setApprove(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void getTest() throws Exception {
        when(bookingService.get(anyInt(), anyInt())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));

        verify(bookingService, times(1)).get(anyInt(), anyInt());
    }

    @Test
    void getForUser() throws Exception {
        when(bookingService.getForUser(anyInt(), anyString()))
                .thenReturn(List.of(bookingDto));
        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingDto.getStatus().toString()));

        verify(bookingService, times(1)).getForUser(anyInt(), anyString());
    }

    @Test
    void getForOwner() throws Exception {
        when(bookingService.getForOwner(anyInt(), anyString()))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingDto.getStatus().toString()));

        verify(bookingService, times(1)).getForOwner(anyInt(), anyString());
    }
}