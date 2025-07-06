package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    public static final String USER_ID = "X-Sharer-User-Id";

    private final ItemService service;

    @PostMapping
    public ItemDto add(@Valid @RequestBody Item item, @RequestHeader(USER_ID) int userId) {
        return service.add(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody UpdateItemDto updateItemDto,
                          @RequestHeader(USER_ID) int userId,
                          @PathVariable int itemId) {
        return service.update(updateItemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable int itemId) {
        return service.get(itemId);
    }

    @GetMapping
    public List<ItemDtoOwners> getByOwner(@RequestHeader(USER_ID) int userId) {
        return service.getByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }
}
