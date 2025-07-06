package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOwners;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    @Override
    public ItemDto add(Item item, int userId) {
        User user = userRepository.get(userId).orElseThrow(()
                -> new NotFoundException("userId=" + userId + " не найден"));
        item.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.add(item));
    }

    @Override
    public ItemDto get(int id) {
        return ItemMapper.toItemDto(itemRepository.get(id).orElseThrow(()
                -> new NotFoundException("itemId=" + id + " не найден")));
    }

    @Override
    public ItemDto update(UpdateItemDto updateItemDto, int userId, int itemId) {
        userRepository.get(userId).orElseThrow(()
                -> new NotFoundException("userId=" + userId + " не найден"));
        Item item = itemRepository.get(itemId).orElseThrow(()
                -> new NotFoundException("itemId=" + itemId + " не найден"));
        if (item.getOwner().getId() != userId) {
            throw new NotAccessException("Ошибка доступа: вещь id={0} не принадлежит userId={1}"
                    .formatted(itemId, userId));
        }
        if (updateItemDto.getName() != null) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDtoOwners> getByOwner(int ownerId) {
        userRepository.get(ownerId).orElseThrow(()
                -> new NotFoundException("userId=" + ownerId + " не найден"));
        List<Item> items = itemRepository.getAll();
        return items.stream()
                .filter(i -> i.getOwner().getId() == ownerId)
                .map(ItemMapper::toItemDtoOwners)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.getAll();
        return items.stream()
                .filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(text.toLowerCase()))
                        || (i.getDescription() != null && i.getDescription().toLowerCase().contains(text.toLowerCase())))
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}
