package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public Booking createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(id) int userId) {
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoGet setApprove(@RequestHeader(id) int userId, @PathVariable int bookingId,
                                    @RequestParam Boolean approved) {
        return bookingService.setApprove(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoGet getForOwnerOrBooker(@PathVariable int bookingId, @RequestHeader(id) int userId){
        return bookingService.getForOwnerOrBooker(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoGet> getForUser(@RequestHeader(id) int userId,
                                          @RequestParam(defaultValue = "ALL") String state){
         return bookingService.getForUser(state, userId);
    }

   @GetMapping("/owner")
    public List<BookingDtoGet> getBookingsForOwner(@RequestHeader(id) int userId,
                                                   @RequestParam(defaultValue = "ALL") String state){
        return bookingService.getBookingsForOwner(state, userId);
    }
}
