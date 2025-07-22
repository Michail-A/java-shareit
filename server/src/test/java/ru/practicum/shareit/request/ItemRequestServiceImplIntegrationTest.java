package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final EntityManager em;

    private UserDto user1;
    private UserDto user2;
    private AddItemRequestDto addItemRequestDto1;
    private AddItemRequestDto addItemRequestDto2;
    private AddItemRequestDto addItemRequestDto3;
    private AddItemRequestDto addItemRequestDto4;

    @BeforeEach
    void setUp() {
        user1 = userService.add(User.builder()
                .name("test")
                .email("test@mail.ru")
                .build());

        user2 = userService.add(User.builder()
                .name("test2")
                .email("test2@mail.ru")
                .build());

        addItemRequestDto1 = new AddItemRequestDto();
        addItemRequestDto1.setDescription("Test");

        addItemRequestDto2 = new AddItemRequestDto();
        addItemRequestDto2.setDescription("Test");

        addItemRequestDto3 = new AddItemRequestDto();
        addItemRequestDto3.setDescription("Test");

        addItemRequestDto4 = new AddItemRequestDto();
        addItemRequestDto4.setDescription("Test");


        itemRequestService.addRequest(addItemRequestDto1, user1.getId());
        itemRequestService.addRequest(addItemRequestDto2, user1.getId());
        itemRequestService.addRequest(addItemRequestDto3, user1.getId());
        itemRequestService.addRequest(addItemRequestDto4, user2.getId());
    }

    @Test
    void getAllRequests() {
        List<GetItemRequestDto> requestsForUser2 = itemRequestService.getAllRequests(user2.getId());

        TypedQuery<ItemRequest> query = em
                .createQuery("Select r from ItemRequest r where r.requester.id != :id Order By created Desc", ItemRequest.class);
        List<ItemRequest> requests = query.setParameter("id", user2.getId()).getResultList();

        assertEquals(requestsForUser2.get(0), ItemRequestMapper.toGetItemRequestDto(requests.get(0)));
        assertEquals(requestsForUser2.get(1), ItemRequestMapper.toGetItemRequestDto(requests.get(1)));
        assertEquals(requestsForUser2.get(2), ItemRequestMapper.toGetItemRequestDto(requests.get(2)));
    }

    @Test
    void getRequestsByOwner() {
        List<GetItemRequestDto> requests = itemRequestService.getRequestsByOwner(user1.getId());
        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.requester.id = :id Order By created Desc", ItemRequest.class);
        List<ItemRequest> expected = query.setParameter("id", user1.getId()).getResultList();
        assertEquals(expected.size(), requests.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).getId(), requests.get(i).getId());
            assertEquals(expected.get(i).getDescription(), requests.get(i).getDescription());
        }
    }

    @Test
    void getRequestById() {

        TypedQuery<ItemRequest> query = em.createQuery("Select r from ItemRequest r where r.requester.id = :id Order By created Desc", ItemRequest.class);
        List<ItemRequest> expected = query.setParameter("id", user1.getId()).getResultList();
        int requestId = expected.get(0).getId();
        GetItemRequestDto requestDto = itemRequestService.getRequestById(requestId, user1.getId());
        assertEquals(requestId, requestDto.getId());
        assertEquals(expected.get(0).getDescription(), requestDto.getDescription());
    }

    @Test
    void addRequestShouldAddSuccessfully() {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto();
        addItemRequestDto.setDescription("new request");
        GetItemRequestDto result = itemRequestService.addRequest(addItemRequestDto, user1.getId());
        assertThat(result.getDescription()).isEqualTo("new request");
    }

    @Test
    void addRequestShouldThrowNotFoundExceptionIfUserNotFound() {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto();
        addItemRequestDto.setDescription("desc");
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.addRequest(addItemRequestDto, 999)
        );
        assertThat(exception.getMessage()).contains("Пользователь id= 999 не найден.");
    }

    @Test
    void getRequestByIdShouldReturnRequest() {
        List<GetItemRequestDto> requests = itemRequestService.getRequestsByOwner(user1.getId());
        int requestId = requests.get(0).getId();
        GetItemRequestDto result = itemRequestService.getRequestById(requestId, user1.getId());
        assertEquals(requestId, result.getId());
    }

    @Test
    void getRequestByIdShouldThrowNotFoundExceptionIfUserNotFound() {
        List<GetItemRequestDto> requests = itemRequestService.getRequestsByOwner(user1.getId());
        int requestId = requests.get(0).getId();
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestById(requestId, 999)
        );
        assertThat(exception.getMessage()).contains("Пользователь id= 999 не найден.");
    }

    @Test
    void getRequestByIdShouldThrowNotFoundExceptionIfRequestNotFound() {
        Exception exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getRequestById(999, user1.getId())
        );
        assertThat(exception.getMessage()).contains("Запрос id= 999 не найден.");
    }

    @Test
    void getRequestsDtoShouldReturnRequestsWithoutItemsIfItemsEmpty() {
        List<ItemRequest> requests = em.createQuery("select r from ItemRequest r", ItemRequest.class).getResultList();
        List<Item> items = Collections.emptyList();
        List<GetItemRequestDto> result = ItemRequestServiceImpl.getRequestsDto(requests, items);
        for (GetItemRequestDto dto : result) {
            assertThat(dto.getItems()).isEmpty();
        }
    }

    @Test
    void getRequestsDtoShouldReturnRequestsWithItemsIfItemsPresent() {

        User user = em.find(User.class, user1.getId());
        ItemRequest request = ItemRequest.builder()
                .description("desc")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);
        em.flush();
        Item item = Item.builder()
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        em.persist(item);
        em.flush();
        List<ItemRequest> requests = Collections.singletonList(request);
        List<Item> items = Collections.singletonList(item);
        List<GetItemRequestDto> result = ItemRequestServiceImpl.getRequestsDto(requests, items);
        assertThat(result.get(0).getItems()).isNotEmpty();
        assertThat(result.get(0).getItems().get(0).getName()).isEqualTo("item");
    }

    @Test
    void getRequestByIdShouldReturnRequestWithItemsIfPresent() {

        User user = em.find(User.class, user1.getId());
        ItemRequest request = ItemRequest.builder()
                .description("desc")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);
        em.flush();
        Item item = Item.builder()
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .request(request)
                .build();
        em.persist(item);
        em.flush();
        GetItemRequestDto result = itemRequestService.getRequestById(request.getId(), user1.getId());
        assertThat(result.getItems()).isNotEmpty();
        assertThat(result.getItems().get(0).getName()).isEqualTo("item");
    }

    @Test
    void getRequestByIdShouldReturnRequestWithoutItemsIfNone() {

        User user = em.find(User.class, user1.getId());
        ItemRequest request = ItemRequest.builder()
                .description("desc2")
                .requester(user)
                .created(LocalDateTime.now())
                .build();
        em.persist(request);
        em.flush();
        GetItemRequestDto result = itemRequestService.getRequestById(request.getId(), user1.getId());
        assertThat(result.getItems()).isEmpty();
    }
}