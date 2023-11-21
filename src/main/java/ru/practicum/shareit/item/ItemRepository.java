package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(int ownerId);

    @Query(value = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<Item> findItemsByText(@Param("text") String text);
}
