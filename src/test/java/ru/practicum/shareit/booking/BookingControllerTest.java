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
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.BookingUserDto;

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

    BookingDto bookingDto;
    BookingDtoGet bookingDtoGet;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
        bookingDtoGet = new BookingDtoGet();
        bookingDtoGet.setId(1);
        bookingDtoGet.setStart(bookingDto.getStart());
        bookingDtoGet.setEnd(bookingDto.getEnd());
        bookingDtoGet.setStatus(Status.WAITING);
        bookingDtoGet.setBooker(new BookingUserDto(1));
        bookingDtoGet.setItem(new BookingItemDto(1, "Test"));
    }

    @Test
    void createBooking() throws Exception {
        Booking booking = new Booking();
        booking.setId(1);
        booking.setStart(bookingDtoGet.getStart());
        booking.setEnd(bookingDtoGet.getEnd());
        booking.setItem(new Item());
        booking.setBooker(new User());
        booking.setStatus(bookingDtoGet.getStatus());

        when(bookingService.create(any(BookingDto.class), anyInt())).thenReturn(booking);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.status").value(booking.getStatus().toString()));


        verify(bookingService, times(1)).create(any(BookingDto.class), anyInt());
    }

    @Test
    void setApprove() throws Exception {
        bookingDtoGet.setStatus(Status.APPROVED);
        when(bookingService.setApprove(anyInt(), anyInt(), anyBoolean())).thenReturn(bookingDtoGet);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoGet.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoGet.getStatus().toString()));

        verify(bookingService, times(1)).setApprove(anyInt(), anyInt(), anyBoolean());
    }

    @Test
    void getForOwnerOrBooker() throws Exception {
        when(bookingService.getForOwnerOrBooker(anyInt(), anyInt())).thenReturn(bookingDtoGet);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(jsonPath("$.id").value(bookingDtoGet.getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoGet.getStatus().toString()));

        verify(bookingService, times(1)).getForOwnerOrBooker(anyInt(), anyInt());
    }

    @Test
    void getForUser() throws Exception {
        when(bookingService.getForUser(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoGet));
        mockMvc.perform(get("/bookings?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(bookingDtoGet.getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingDtoGet.getStatus().toString()));

        verify(bookingService, times(1)).getForUser(anyString(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void getBookingsForOwner() throws Exception {
        when(bookingService.getBookingsForOwner(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingDtoGet));

        mockMvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(bookingDtoGet.getId()))
                .andExpect(jsonPath("$.[0].status").value(bookingDtoGet.getStatus().toString()));

        verify(bookingService, times(1)).getBookingsForOwner(anyString(), anyInt(), anyInt(), anyInt());
    }
}