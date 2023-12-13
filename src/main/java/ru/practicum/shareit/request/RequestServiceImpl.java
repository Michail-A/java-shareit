package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

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
        return requests
                .stream()
                .map(RequestMapper::mapToGetRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<GetRequestDto> getAllRequests(int from, int size, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        Page<Request> requests = requestRepository
                .findByRequesterIdNotOrderByCreatedDesc(userId, PageRequest.of(from, size));

        return requests
                .getContent()
                .stream()
                .map(RequestMapper::mapToGetRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public GetRequestDto getRequestById(int requestId, int userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));

        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос id= " + requestId + " не найден."));

        return RequestMapper.mapToGetRequestDto(request);
    }
}
