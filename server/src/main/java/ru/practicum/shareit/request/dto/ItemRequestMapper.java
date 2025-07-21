package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ItemRequestMapper {

    public static ItemRequest toNewItemRequest(AddItemRequestDto addRequestDto, User requester) {
        return ItemRequest.builder()
                .description(addRequestDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();
    }

    public static GetItemRequestDto toGetItemRequestDto(ItemRequest request) {
        GetItemRequestDto getRequestDto = new GetItemRequestDto();
        getRequestDto.setId(request.getId());
        getRequestDto.setDescription(request.getDescription());
        getRequestDto.setCreated(request.getCreated());
        getRequestDto.setItems(new ArrayList<>());
        return getRequestDto;
    }
}
