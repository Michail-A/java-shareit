package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoAdd;
import ru.practicum.shareit.item.dto.CommentDtoGet;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String id = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDtoGet create(@Valid @RequestBody ItemDtoAdd itemDto, @RequestHeader(id) int userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestBody ItemDtoAdd itemDto, @RequestHeader(id) int userId,
                       @PathVariable int itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoGet get(@PathVariable int itemId, @RequestHeader(id) int userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoGet> getByUser(@RequestHeader(id) int userId,
                                      @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size) {
        return itemService.getByUser(userId, from, size);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text,
                             @RequestParam(defaultValue = "0") @Min(0) int from,
                             @RequestParam(defaultValue = "20") @Min(1) @Max(20) int size) {
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoGet addComment(@RequestBody @Valid CommentDtoAdd commentDtoAdd, @PathVariable int itemId, @RequestHeader(id) int userId) {
        return itemService.addComment(itemId, userId, commentDtoAdd);
    }
}
