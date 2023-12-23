package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoAdd;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemClient client;
    private static final String id = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDtoAdd itemDto, @RequestHeader(id) int userId) {
        return client.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDtoAdd itemDto, @RequestHeader(id) int userId,
                             @PathVariable int itemId) {
        return client.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable int itemId, @RequestHeader(id) int userId) {
        return client.get(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader(id) int userId,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size) {
        return client.getByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                   @RequestParam(defaultValue = "0") @Min(0) int from,
                                   @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size) {
        int userId = 0;
        return client.search(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDtoAdd commentDtoAdd, @PathVariable int itemId, @RequestHeader(id) int userId) {
        return client.addComment(commentDtoAdd, itemId, userId);
    }
}
