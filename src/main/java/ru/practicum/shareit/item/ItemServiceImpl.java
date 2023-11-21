package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public Item create(ItemDto itemDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        Item item = itemRepository.save(ItemMapper.mapToNewItem(itemDto, user));
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, int userId, int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        item.setOwner(user);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException("У пользователя нет такой вещи");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.saveAndFlush(item);
    }

    @Override
    public Item get(int itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
    }

    @Override
    public List<Item> getByUser(int userId) {
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.findItemsByText(text);
        }
    }
}
