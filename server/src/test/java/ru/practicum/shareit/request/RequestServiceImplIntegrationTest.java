package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.AddRequestDto;
import ru.practicum.shareit.request.dto.GetRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplIntegrationTest {
    private final RequestService requestService;
    private final UserService userService;
    private final EntityManager em;

    private UserDto userDto;
    private UserDto userDto2;
    private AddRequestDto addRequestDto1;
    private AddRequestDto addRequestDto2;
    private AddRequestDto addRequestDto3;
    private AddRequestDto addRequestDto4;

    @BeforeEach
    void seetUp() {
        userDto = new UserDto("Test", "Test");
        userDto2 = new UserDto("2", "2");
        addRequestDto1 = new AddRequestDto();
        addRequestDto1.setDescription("Test");

        addRequestDto2 = new AddRequestDto();
        addRequestDto2.setDescription("Test");

        addRequestDto3 = new AddRequestDto();
        addRequestDto3.setDescription("Test");

        addRequestDto4 = new AddRequestDto();
        addRequestDto4.setDescription("Test");

        userService.create(userDto);
        userService.create(userDto2);

        requestService.addRequest(addRequestDto1, 1);
        requestService.addRequest(addRequestDto2, 1);
        requestService.addRequest(addRequestDto3, 1);
        requestService.addRequest(addRequestDto4, 2);
    }

    @Test
    void getAllRequests() {
        List<GetRequestDto> requestsForUser2 = requestService.getAllRequests(0, 20, 2);

        TypedQuery<Request> query = em
                .createQuery("Select r from Request r where r.requester.id != :id Order By Created Desc", Request.class);
        List<Request> requests = query.setParameter("id", 2).getResultList();

        assertEquals(requestsForUser2.get(0), RequestMapper.mapToGetRequestDto(requests.get(0)));
        assertEquals(requestsForUser2.get(1), RequestMapper.mapToGetRequestDto(requests.get(1)));
        assertEquals(requestsForUser2.get(2), RequestMapper.mapToGetRequestDto(requests.get(2)));

    }

}