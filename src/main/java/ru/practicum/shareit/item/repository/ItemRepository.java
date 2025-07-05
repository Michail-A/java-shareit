package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> add(Item item);

    Optional<Item> get(int id);


    List<Item> getAll();
}
