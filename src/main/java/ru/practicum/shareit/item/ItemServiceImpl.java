package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoGet;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public Item create(ItemDto itemDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        Item item = itemRepository.save(ItemMapper.mapToNewItem(itemDto, user));
        return item;
    }

    @Override
    public Item update(ItemDto itemDto, int userId, int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        item.setOwner(user);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException("У пользователя нет такой вещи");
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemRepository.saveAndFlush(item);
    }

    @Override
    public ItemDtoGet get(int itemId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));

         Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
         LocalDateTime current = LocalDateTime.now();
         ItemDtoGet itemDtoGet = ItemMapper.mapToGetItemDtoBooking(item, null, null);
         if(item.getOwner().getId() == userId){
             List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());
             Booking lastBooking = bookings
                     .stream()
                     .sorted(orderByStartDesc)
                     .filter(t -> t.getStart().isBefore(current) &&
                             t.getStatus().equals(Status.APPROVED))
                     .findFirst()
                     .orElse(null);

             Booking nextBooking = bookings
                     .stream()
                     .sorted(orderByStartAsc)
                     .filter(t -> t.getStart().isAfter(current) &&
                             t.getStatus().equals(Status.APPROVED))
                     .findFirst()
                     .orElse(null);

             itemDtoGet.setLastBooking(BookingMapper.mapToItemBookingDtoGet(lastBooking));
             itemDtoGet.setNextBooking(BookingMapper.mapToItemBookingDtoGet(nextBooking));
         }
         return itemDtoGet;
    }

    @Override
    public List<ItemDtoGet> getByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));

        LocalDateTime current = LocalDateTime.now();
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDtoGet> itemsWithBookings = new ArrayList<>();
        for (Item item : items) {

            List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());

            Booking lastBooking = bookings
                    .stream()
                    .sorted(orderByStartDesc)
                    .filter(t -> t.getStart().isBefore(current) &&
                            t.getStatus().equals(Status.APPROVED))
                    .findFirst()
                    .orElse(null);

            Booking nextBooking = bookings
                    .stream()
                    .sorted(orderByStartAsc)
                    .filter(t -> t.getStart().isAfter(current) &&
                            t.getStatus().equals(Status.APPROVED))
                    .findFirst()
                    .orElse(null);

            itemsWithBookings.add(ItemMapper.mapToGetItemDtoBooking(item,
                    BookingMapper.mapToItemBookingDtoGet(lastBooking),
                    BookingMapper.mapToItemBookingDtoGet(nextBooking)));
        }

        return itemsWithBookings;
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return List.of();
        } else {
            return itemRepository.findItemsByText(text);
        }
    }

    public static final Comparator<Booking> orderByStartDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };

    public static final Comparator<Booking> orderByStartAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };
}
