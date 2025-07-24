package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    GetItemRequestDto addRequest(AddItemRequestDto addRequestDto, int requesterId);

    List<GetItemRequestDto> getRequestsByOwner(int requesterId);

    List<GetItemRequestDto> getAllRequests(int userId);

    GetItemRequestDto getRequestById(int requestId, int userId);
}
