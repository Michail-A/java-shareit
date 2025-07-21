package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoAdd bookingDtoAdd, @RequestHeader(id) int userId) {
        return bookingService.add(bookingDtoAdd, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setApprove(@RequestHeader(id) int userId, @PathVariable int bookingId,
                                 @RequestParam Boolean approved) {
        return bookingService.setApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getForOwnerOrBooker(@PathVariable int bookingId, @RequestHeader(id) int userId) {
        return bookingService.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getForUser(@RequestHeader(id) int userId,
                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForOwner(@RequestHeader(id) int userId,
                                                @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getForOwner(userId, state);
    }
}
