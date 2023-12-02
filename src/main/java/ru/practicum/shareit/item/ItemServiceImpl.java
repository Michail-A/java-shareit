package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.error.CommentAccessError;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDtoGet addComment(int itemId, int userId, CommentDtoAdd commentDtoAdd) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(itemId);
        if (bookings.stream().anyMatch(booking -> booking.getBooker().equals(user) && booking.getEnd().isBefore(now))) {
            Comment comment = ItemMapper.mapToNewComment(commentDtoAdd, item, user);
            return ItemMapper.mapToCommentDtoGet(commentRepository.save(comment));
        } else {
            throw new CommentAccessError("Пользователь " + userId + "не брал вещь " + itemId + " в аренду");
        }
    }

    @Transactional
    @Override
    public Item create(ItemDtoAdd itemDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        Item item = itemRepository.save(ItemMapper.mapToNewItem(itemDto, user));
        return item;
    }

    @Override
    public Item update(ItemDtoAdd itemDto, int userId, int itemId) {
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
                () -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        LocalDateTime current = LocalDateTime.now();
        ItemDtoGet itemDtoGet = ItemMapper.mapToGetItemDtoBooking(item, null, null, null);
        if (item.getOwner().getId() == userId) {
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
        List<Comment> comments = new ArrayList<>(commentRepository.findAllByItemIdOrderByIdDesc(itemId));
        List<CommentDtoGet> commentsDtoGet = new ArrayList<>();
        if (!comments.isEmpty()) {
            commentsDtoGet.addAll(comments
                    .stream()
                    .map(ItemMapper::mapToCommentDtoGet)
                    .collect(Collectors.toList()));

        }
        itemDtoGet.setComments(commentsDtoGet);
        return itemDtoGet;
    }

    @Override
    public List<ItemDtoGet> getByUser(int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

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

            List<Comment> comments = commentRepository.findAllByItemIdOrderByIdDesc(item.getId());
            List<CommentDtoGet> commentsDtoGet = new ArrayList<>();
            if (!comments.isEmpty()) {
                commentsDtoGet.addAll(comments
                        .stream()
                        .map(ItemMapper::mapToCommentDtoGet)
                        .collect(Collectors.toList()));
            }
            itemsWithBookings.add(ItemMapper.mapToGetItemDtoBooking(item,
                    BookingMapper.mapToItemBookingDtoGet(lastBooking),
                    BookingMapper.mapToItemBookingDtoGet(nextBooking), commentsDtoGet));

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
