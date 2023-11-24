package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private static final String id = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public Item create(@Valid @RequestBody ItemDto itemDto, @RequestHeader(id) int userId) {
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestBody ItemDto itemDto, @RequestHeader(id) int userId,
                       @PathVariable int itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoGet get(@PathVariable int itemId, @RequestHeader(id) int userId) {
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoGet> getByUser(@RequestHeader(id) int userId) {
        return itemService.getByUser(userId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        return itemService.search(text);
    }
}
