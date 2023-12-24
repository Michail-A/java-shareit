package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;

import java.util.List;

public interface RequestService {
    GetRequestDto addRequest(AddRequestDto addRequestDto, int requesterId);

    List<GetRequestDto> getRequestsByOwner(int requesterId);

    List<GetRequestDto> getAllRequests(int from, int size, int userId);

    GetRequestDto getRequestById(int requestId, int userId);
}
