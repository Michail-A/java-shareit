package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.ItemBookingDtoGet;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable()
               // item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item mapToNewItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);
        return item;
    }

    public static BookingItemDto mapToBookingItemDto(Item item){
        return new BookingItemDto(item.getId(), item.getName());
    }

    public static ItemDtoGet mapToGetItemDtoBooking(Item item, ItemBookingDtoGet lastBooking,
                                                    ItemBookingDtoGet nextBooking){
        return new ItemDtoGet(item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), lastBooking, nextBooking);
    }
}
