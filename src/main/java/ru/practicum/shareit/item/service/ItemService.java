package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto add(Item item, int userId);

    ItemDto get(int id);

    ItemDto update(UpdateItemDto updateItemDto, int userId, int itemId);

    List<ItemDtoOwners> getByOwner(int ownerId);

    List<ItemDto> search(String text);


}
