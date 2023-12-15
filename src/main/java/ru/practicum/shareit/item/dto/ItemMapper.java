package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemBookingDtoGet;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        ItemDtoGet itemDtoGet =  new ItemDtoGet(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), lastBooking, nextBooking, null, comments);
        if(item.getRequest() != null){
            itemDtoGet.setRequestId(item.getRequest().getId());
        }
        return itemDtoGet;
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

    public static RequestDtoItemGet mapToRequestDtoItemGet(Item item){
        RequestDtoItemGet requestDtoItemGet = new RequestDtoItemGet();
        requestDtoItemGet.setId(item.getId());
        requestDtoItemGet.setName(item.getName());
        requestDtoItemGet.setDescription(item.getDescription());
        requestDtoItemGet.setAvailable(item.isAvailable());
        if(item.getRequest() != null){
            requestDtoItemGet.setRequestId(item.getRequest().getId());
        }
        return requestDtoItemGet;
    }

    public static ItemDtoGet mapToGetItemDto(Item item){
        ItemDtoGet itemDtoGet = new ItemDtoGet(item.getId(),
                item.getName(), item.getDescription(),
                item.isAvailable(), new ItemBookingDtoGet(),
                new ItemBookingDtoGet(), null,
                new ArrayList<>());
        if(item.getRequest() != null){
            itemDtoGet.setRequestId(item.getRequest().getId());
        }
        return itemDtoGet;
    }
}
