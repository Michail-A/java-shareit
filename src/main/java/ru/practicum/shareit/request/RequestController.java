package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {

    private final RequestService requestService;

    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public GetRequestDto addRequest(@RequestBody @Valid AddRequestDto addRequestDto, @RequestHeader(id) int requesterId) {
        return requestService.addRequest(addRequestDto, requesterId);
    }

    @GetMapping
    public List<GetRequestDto> getRequestsByOwner(@RequestHeader(id) int requesterId) {
        return requestService.getRequestsByOwner(requesterId);
    }

    @GetMapping("/all")
    public List<GetRequestDto> getAllRequests(@RequestParam(defaultValue = "0") @Min(0) int from,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size,
                                              @RequestHeader(id) int userId) {
        return requestService.getAllRequests(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public GetRequestDto getRequestById(@PathVariable int requestId, @RequestHeader(id) int userId) {
        return requestService.getRequestById(requestId, userId);
    }

}
