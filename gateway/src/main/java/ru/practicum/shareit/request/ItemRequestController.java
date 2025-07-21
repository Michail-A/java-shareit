package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddRequestDto;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestClient client;

    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestBody @Valid AddRequestDto addRequestDto, @RequestHeader(id) int requesterId) {
        return client.addRequest(addRequestDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByOwner(@RequestHeader(id) int requesterId) {
        return client.getRequestsByOwner(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestParam(defaultValue = "0") @Min(0) int from,
                                                 @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size,
                                                 @RequestHeader(id) int userId) {
        return client.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@PathVariable int requestId, @RequestHeader(id) int userId) {
        return client.getRequestById(requestId, userId);
    }

}
