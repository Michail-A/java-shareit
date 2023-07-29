package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ItemDaoImpl implements ItemDao {

    private Map<Integer, Item> storage = new HashMap<>();
    int id = 1;

    @Override
    public Item create(ItemDto itemDto, User user) {
        validate(itemDto);
        Item item = new Item(id, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(),
                user, itemDto.getRequest());
        storage.put(id, item);
        id++;
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, int userId, int itemId) {
        Item item = storage.get(itemId);
        if (item.getOwner().getId() != userId) {
            throw new NotFoundUserException("Вещь не принадлежит пользователю id= " + userId);
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        storage.put(itemId, item);
        return item;
    }

    @Override
    public Item get(int itemId) {
        return storage.get(itemId);
    }

    @Override
    public List<Item> getByUser(int userId) {
        List<Item> items = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.getOwner().getId() == userId) {
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> items = new ArrayList<>();
        for (Item item : storage.values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                    item.getDescription().toLowerCase().contains(text.toLowerCase()) && item.isAvailable()) {
                items.add(item);
            }
        }
        return items;
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ItemException("Ошибка имени");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ItemException("Ошибка описания");
        }
        if (itemDto.getAvailable() == null) {
            throw new ItemException("Отсутствует статус");
        }
    }
}
