package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByItemIdOrderByIdDesc(int itemId);

    @Query(value = "select c from Comment c where c.item.owner.id = ?1")
    List<Comment> findByOwnerId(int userId);
}
