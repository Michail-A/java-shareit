package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemBookingDtoGet;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {

    public static Item mapToNewItem(ItemDtoAdd itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static BookingItemDto mapToBookingItemDto(Item item) {
        return new BookingItemDto(item.getId(), item.getName());
    }

    public static ItemDtoGet mapToGetItemDtoBooking(Item item, ItemBookingDtoGet lastBooking,
                                                    ItemBookingDtoGet nextBooking, List<CommentDtoGet> comments) {
        return new ItemDtoGet(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), lastBooking, nextBooking, comments);
    }

    public static Comment mapToNewComment(CommentDtoAdd commentDtoAdd, Item item, User author) {
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment();
        comment.setText(commentDtoAdd.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(created);
        return comment;
    }

    public static CommentDtoGet mapToCommentDtoGet(Comment comment) {
        return new CommentDtoGet(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}
