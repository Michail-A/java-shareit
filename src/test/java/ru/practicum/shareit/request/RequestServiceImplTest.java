package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    RequestRepository requestRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    RequestServiceImpl requestService;

    Item item;
    User user;
    AddRequestDto addRequestDto;
    Request request;
    Request request2;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test");
        user.setEmail("test@test.com");

        item = new Item();
        item.setId(1);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(new User());

        addRequestDto = new AddRequestDto();
        addRequestDto.setDescription("Test");

        request = new Request();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription(addRequestDto.getDescription());
        request.setId(1);

        request2 = new Request();
        request2.setRequester(new User());
        request2.setCreated(LocalDateTime.now());
        request2.setDescription("Test");
        request2.setId(2);
    }

    @Test
    void addRequest() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        GetRequestDto getRequestDto = requestService.addRequest(addRequestDto, user.getId());
        assertEquals(getRequestDto.getDescription(), request.getDescription());
        verify(requestRepository).save(any(Request.class));
    }

    @Test
    void addRequestFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.addRequest(addRequestDto, user.getId()));
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void getRequestsByOwner() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findByRequesterIdOrderByCreatedDesc(anyInt())).thenReturn(List.of(request));
        when(itemRepository.findByRequesterId(anyInt())).thenReturn(List.of(item));

        List<GetRequestDto> getRequestDtos = requestService.getRequestsByOwner(user.getId());
        assertEquals(getRequestDtos.get(0), RequestMapper.mapToGetRequestDto(request));
    }

    @Test
    void getRequestsByOwnerFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.getRequestsByOwner(user.getId()));
    }

    @Test
    void getAllRequests() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        Page<Request> requests = new PageImpl<>(List.of(request2));
        when(requestRepository.findByRequesterIdNotOrderByCreatedDesc(anyInt(), any(Pageable.class)))
                .thenReturn(requests);
        when(itemRepository.findAll()).thenReturn(List.of());

        List<GetRequestDto> getRequestDtos = requestService.getAllRequests(0, 10, user.getId());
        assertEquals(getRequestDtos.get(0), RequestMapper.mapToGetRequestDto(requests.getContent().get(0)));
    }

    @Test
    void getAllRequestsFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.getAllRequests(0, 10, user.getId()));
    }

    @Test
    void getRequestById() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(request));
        when(itemRepository.findByRequestId(anyInt())).thenReturn(List.of(item));


        GetRequestDto getRequestDto = requestService.getRequestById(request2.getId(), user.getId());
        assertEquals(getRequestDto.getDescription(), request.getDescription());
    }

    @Test
    void getRequestByIdFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), user.getId()));
    }

    @Test
    void getRequestByIdFailRequestNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), user.getId()));
    }
}