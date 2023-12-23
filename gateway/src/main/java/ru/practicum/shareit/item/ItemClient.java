package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoAdd;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDtoAdd itemDtoAdd, int userId) {
        return post("", userId, itemDtoAdd);
    }

    public ResponseEntity<Object> update(ItemDtoAdd itemDtoAdd, int userId, int itemId) {
        return patch("/" + itemId, userId, itemDtoAdd);
    }

    public ResponseEntity<Object> get(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getByUser(int userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> search(String text, int from, int size, int userId) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        return get("/search?text={text}&from={from}&size={size}", (long) userId, parameters);
    }

    public ResponseEntity<Object> addComment(CommentDtoAdd commentDtoAdd, int itemId, int userId) {
        return post("/ " + itemId + "/comment", userId, commentDtoAdd);
    }
}
