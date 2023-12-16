package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDtoAdd;
import ru.practicum.shareit.item.dto.CommentDtoGet;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDtoGet create(ItemDtoAdd itemDto, int userId);

    Item update(ItemDtoAdd itemDto, int userId, int itemId);

    ItemDtoGet get(int itemId, int userId);

    List<ItemDtoGet> getByUser(int userId, int from, int size);

    List<Item> search(String text, int from, int size);

    CommentDtoGet addComment(int itemId, int userId, CommentDtoAdd commentDtoAdd);
}
