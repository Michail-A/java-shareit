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
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class RequestServiceImplIntegrationTest {
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
        // Сохраняем пользователей и получаем их ID
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
}
