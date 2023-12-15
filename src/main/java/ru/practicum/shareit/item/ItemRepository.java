package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findAllByOwnerId(int ownerId, Pageable pageable);

    @Query(value = "SELECT item FROM Item item " +
            "WHERE item.available = TRUE " +
            "AND (UPPER(item.name) LIKE UPPER(CONCAT('%', :text, '%')) " +
            "OR UPPER(item.description) LIKE UPPER(CONCAT('%', :text, '%')))")
    List<Item> findItemsByText(@Param("text") String text);

    @Query(value = "select i from Item i where i.request.requester.id = ?1 " +
            "order by i.id desc")
    List<Item> findByRequesterId(int userId);

    List<Item> findByRequestId(int requestId);
}
