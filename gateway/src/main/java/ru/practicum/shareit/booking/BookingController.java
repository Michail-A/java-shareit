package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingClient client;
    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(id) int userId) {
        return client.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestHeader(id) int userId, @PathVariable int bookingId,
                                    @RequestParam Boolean approved) {
        return client.setApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getForOwnerOrBooker(@PathVariable int bookingId, @RequestHeader(id) int userId) {
        return client.getForOwnerOrBooker(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getForUser(@RequestHeader(id) int userId,
                                             @RequestParam(defaultValue = "ALL") String state,
                                             @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size)  {
        return client.getForUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object>getBookingsForOwner(@RequestHeader(id) int userId,
                                                     @RequestParam(defaultValue = "ALL") String state,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size)  {
        return client.getBookingsForOwner(userId, state, from, size);
    }
}
