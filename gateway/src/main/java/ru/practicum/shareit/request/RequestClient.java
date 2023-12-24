package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.AddRequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addRequest(AddRequestDto addRequestDto, int requesterId) {
        return post("", requesterId, addRequestDto);
    }

    public ResponseEntity<Object> getRequestsByOwner(int requesterId) {
        return get("", requesterId);
    }

    public ResponseEntity<Object> getAllRequests(int from, int size, int userId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(int requestId, int userId) {
        return get("/" + requestId, userId);
    }
}
