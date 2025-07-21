package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDtoItemGet;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public GetItemRequestDto addRequest(AddItemRequestDto addRequestDto, int requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + requesterId + " не найден."));

        ItemRequest request = ItemRequestMapper.toNewItemRequest(addRequestDto, user);
        return ItemRequestMapper.toGetItemRequestDto(requestRepository.save(request));
    }

    @Override
    public List<GetItemRequestDto> getRequestsByOwner(int requesterId) {
        userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + requesterId + " не найден."));


        List<ItemRequest> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        List<Item> items = itemRepository.findByRequestRequesterIdOrderByIdDesc(requesterId);

        return getRequestsDto(requests, items);
    }

    @Override
    public List<GetItemRequestDto> getAllRequests(int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        List<ItemRequest> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId);
        List<Item> items = itemRepository.findAllByRequestIsNotNull();

        return getRequestsDto(requests, items);
    }

    @Override
    public GetItemRequestDto getRequestById(int requestId, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос id= " + requestId + " не найден."));

        List<Item> items = itemRepository.findByRequestId(requestId);
        GetItemRequestDto getRequestDto = ItemRequestMapper.toGetItemRequestDto(request);
        if (!items.isEmpty()) {
            getRequestDto.setItems(items
                    .stream()
                    .map(ItemMapper::toItemRequestDtoItemGet)
                    .collect(Collectors.toList()));
        }
        return getRequestDto;
    }

    public static List<GetItemRequestDto> getRequestsDto(List<ItemRequest> requests, List<Item> items) {
        List<GetItemRequestDto> getRequestsDto = new ArrayList<>();
        Map<ItemRequest, List<Item>> itemsForRequest = new HashMap<>();

        if (!items.isEmpty()) {
            itemsForRequest = items
                    .stream()
                    .collect(groupingBy(Item::getRequest));
        }
        for (ItemRequest request : requests) {
            GetItemRequestDto getRequest = ItemRequestMapper.toGetItemRequestDto(request);
            List<ItemRequestDtoItemGet> requestDtoItemGets = new ArrayList<>();

            if (itemsForRequest.get(request) != null && !itemsForRequest.get(request).isEmpty()) {
                requestDtoItemGets.addAll(itemsForRequest.get(request)
                        .stream()
                        .map(ItemMapper::toItemRequestDtoItemGet)
                        .toList());
                getRequest.setItems(requestDtoItemGets);
            }

            getRequestsDto.add(getRequest);
        }
        return getRequestsDto;
    }
}
