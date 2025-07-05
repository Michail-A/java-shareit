package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Integer, Item> items = new HashMap<>();

    private int id = 1;

    @Override
    public Optional<Item> add(Item item) {
        item.setId(id);
        items.put(id, item);
        id++;
        return Optional.of(item);
    }

    @Override
    public Optional<Item> get(int id) {
        if (!items.containsKey(id)) {
            return Optional.empty();
        }
        return Optional.of(items.get(id));
    }

    @Override
    public List<Item> getAll() {
        return items.values().stream().toList();
    }
}
