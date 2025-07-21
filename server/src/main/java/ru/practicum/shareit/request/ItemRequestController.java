package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService requestService;

    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public GetItemRequestDto addRequest(@RequestBody AddItemRequestDto addRequestDto, @RequestHeader(id) int requesterId) {
        return requestService.addRequest(addRequestDto, requesterId);
    }

    @GetMapping
    public List<GetItemRequestDto> getRequestsByOwner(@RequestHeader(id) int requesterId) {
        return requestService.getRequestsByOwner(requesterId);
    }

    @GetMapping("/all")
    public List<GetItemRequestDto> getAllRequests(@RequestHeader(id) int userId) {
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public GetItemRequestDto getRequestById(@PathVariable int requestId, @RequestHeader(id) int userId) {
        return requestService.getRequestById(requestId, userId);
    }

}
