package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoAdd;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {

    ItemDto add(ItemDtoAdd itemDtoAdd, int userId);

    ItemDto get(int id, int userId);

    ItemDto update(UpdateItemDto updateItemDto, int userId, int itemId);

    List<ItemDtoOwners> getByOwner(int ownerId);

    List<ItemDto> search(String text);

    CommentDtoGet addComment(int itemId, int userId, CommentDtoAdd commentDtoAdd);

}
