package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .build();
    }

    public static ItemDtoOwners toItemDtoOwners(Item item, ItemBookingDtoGet lastBooking,
                                                ItemBookingDtoGet nextBooking, List<CommentDtoGet> comments) {
        return ItemDtoOwners.builder()
                .name(item.getName())
                .description(item.getDescription())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static ItemBookingDtoGet toItemBookingDtoGet(Booking booking, LocalDateTime time) {
        return ItemBookingDtoGet.builder()
                .id(booking.getId())
                .bookerId(booking.getId())
                .time(time)
                .build();
    }

    public static Comment toNewComment(CommentDtoAdd commentDtoAdd, Item item, User author) {
        return Comment.builder()
                .text(commentDtoAdd.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();
    }

    public static CommentDtoGet toCommentDtoGet(Comment comment) {
        return CommentDtoGet.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }


}
