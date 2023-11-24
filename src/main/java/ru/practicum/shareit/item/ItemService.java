package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item create(ItemDto itemDto, int userId);

    Item update(ItemDto itemDto, int userId, int itemId);

    ItemDtoGet get(int itemId, int userId);

    List<ItemDtoGet> getByUser(int userId);

    List<Item> search(String text);
}
