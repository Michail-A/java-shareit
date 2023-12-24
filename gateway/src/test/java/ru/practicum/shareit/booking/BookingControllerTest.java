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

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @MockBean
    private BookingClient client;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
    }

    @Test
    void createBookingFalseNotItemId() throws Exception {
        bookingDto.setItemId(null);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(client, never()).createBooking(any(BookingDto.class), anyInt());
    }

    @Test
    void createBookingFalseStartDate() throws Exception {
        bookingDto.setStart(LocalDateTime.now().minusDays(4));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(client, never()).createBooking(any(BookingDto.class), anyInt());
    }

    @Test
    void createBookingFalseEndDate() throws Exception {
        bookingDto.setEnd(LocalDateTime.now().minusDays(4));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
        verify(client, never()).createBooking(any(BookingDto.class), anyInt());
    }
}