package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemDao {
    Item create(ItemDto itemDto, User user);

    Item update(ItemDto itemDto, int userId, int itemId);

    Item get(int itemId);

    List<Item> getByUser(int userId);

    List<Item> search(String text);
}