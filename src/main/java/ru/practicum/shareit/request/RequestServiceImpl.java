package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.RequestDtoItemGet;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;

    @Override
    public GetRequestDto addRequest(AddRequestDto addRequestDto, int requesterId) {
        User user = userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + requesterId + " не найден."));

        Request request = RequestMapper.mapToNewRequest(addRequestDto, user);
        return RequestMapper.mapToGetRequestDto(requestRepository.save(request));
    }

    @Override
    public List<GetRequestDto> getRequestsByOwner(int requesterId) {
        userRepository.findById(requesterId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + requesterId + " не найден."));


        List<Request> requests = requestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);
        List<Item> items = itemRepository.findByRequestRequesterIdOrderByIdDesc(requesterId);

        return getRequestsDto(requests, items);
    }

    @Override
    public List<GetRequestDto> getAllRequests(int from, int size, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        Page<Request> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));
        List<Item> items = itemRepository.findAllByRequestIsNotNull();

        return getRequestsDto(requests.getContent(), items);
    }

    @Override
    public GetRequestDto getRequestById(int requestId, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос id= " + requestId + " не найден."));

        List<Item> items = itemRepository.findByRequestId(requestId);
        GetRequestDto getRequestDto = RequestMapper.mapToGetRequestDto(request);
        if (!items.isEmpty()) {
            getRequestDto.setItems(items
                    .stream()
                    .map(ItemMapper::mapToRequestDtoItemGet)
                    .collect(Collectors.toList()));
        }
        return getRequestDto;
    }

    public static List<GetRequestDto> getRequestsDto(List<Request> requests, List<Item> items) {
        List<GetRequestDto> getRequestsDto = new ArrayList<>();
        for (Request request : requests) {
            List<RequestDtoItemGet> itemsForRequest = new ArrayList<>();
            if (!items.isEmpty()) {
                itemsForRequest
                        .addAll(items.stream()
                                .filter(item -> item.getRequest() != null && item.getRequest().equals(request))
                                .map(ItemMapper::mapToRequestDtoItemGet)
                                .collect(Collectors.toList()));
            }
            GetRequestDto getRequest = RequestMapper.mapToGetRequestDto(request);
            getRequest.setItems(itemsForRequest);
            getRequestsDto.add(getRequest);
        }
        return getRequestsDto;
    }
}
