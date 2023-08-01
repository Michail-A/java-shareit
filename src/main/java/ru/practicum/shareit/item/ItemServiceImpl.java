package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserDao userDao;
    private final ItemDao itemDao;

    @Override
    public Item create(ItemDto itemDto, int userId) {
        if (!userDao.checkUser(userId)) {
            throw new NotFoundUserException("Такого пользователя не существует");
        }
        User user = userDao.getUser(userId);
        return itemDao.create(itemDto, user);
    }

    @Override
    public Item update(ItemDto itemDto, int userId, int itemId) {
        return itemDao.update(itemDto, userId, itemId);
    }

    @Override
    public Item get(int itemId) {
        return itemDao.get(itemId);
    }

    @Override
    public List<Item> getByUser(int userId) {
        return itemDao.getByUser(userId);
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemDao.search(text);
        }
    }
}
