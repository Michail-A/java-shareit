package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RequestMapper {

    public static Request mapToNewRequest(AddRequestDto addRequestDto, User requester) {
        Request request = new Request();
        request.setDescription(addRequestDto.getDescription());
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static GetRequestDto mapToGetRequestDto(Request request) {
        GetRequestDto getRequestDto = new GetRequestDto();
        getRequestDto.setId(request.getId());
        getRequestDto.setDescription(request.getDescription());
        getRequestDto.setCreated(request.getCreated());
        getRequestDto.setItems(new ArrayList<>());
        return getRequestDto;
    }
}
