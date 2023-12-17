package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.CommentAccessError;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
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
    private final RequestRepository requestRepository;

    @Override
    public CommentDtoGet addComment(int itemId, int userId, CommentDtoAdd commentDtoAdd) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(itemId);
        if (bookings.stream().anyMatch(booking -> booking.getBooker().equals(user) && booking.getEnd().isBefore(now)
                && booking.getStatus().equals(Status.APPROVED))) {
            Comment comment = ItemMapper.mapToNewComment(commentDtoAdd, item, user);
            return ItemMapper.mapToCommentDtoGet(commentRepository.save(comment));
        } else {
            throw new CommentAccessError("Пользователь " + userId + "не брал вещь " + itemId + " в аренду");
        }
    }

    @Transactional
    @Override
    public ItemDtoGet create(ItemDtoAdd itemDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        Item item = ItemMapper.mapToNewItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запрос id= " + itemDto.getRequestId() + " не найден."));
            item.setRequest(request);
        }

        return ItemMapper.mapToGetItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoGet update(ItemDtoAdd itemDto, int userId, int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
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
        return ItemMapper.mapToGetItemDto(itemRepository.saveAndFlush(item));
    }

    @Override
    public ItemDtoGet get(int itemId, int userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        ItemDtoGet itemDtoGet = ItemMapper.mapToGetItemDtoBooking(item, null, null, null);
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());

            itemDtoGet.setLastBooking(BookingMapper.mapToItemBookingDtoGet(setLastBooking(bookings, item)));
            itemDtoGet.setNextBooking(BookingMapper.mapToItemBookingDtoGet(setNextBooking(bookings, item)));
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
    public List<ItemDtoGet> getByUser(int userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findAllByOwnerId(userId, PageRequest.of(from, size)).getContent();
        List<ItemDtoGet> itemsWithBookings = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findByOwnerAll(userId, PageRequest.of(0, 1000)).getContent();
        List<Comment> comments = commentRepository.findByOwnerId(userId);

        for (Item item : items) {

            List<CommentDtoGet> commentsDtoGet = new ArrayList<>();

            if (!comments.isEmpty()) {
                commentsDtoGet.addAll(comments
                        .stream()
                        .filter(comment -> comment.getItem().equals(item))
                        .map(ItemMapper::mapToCommentDtoGet)
                        .collect(Collectors.toList()));
            }

            itemsWithBookings.add(ItemMapper.mapToGetItemDtoBooking(item,
                    BookingMapper.mapToItemBookingDtoGet(setLastBooking(bookings, item)),
                    BookingMapper.mapToItemBookingDtoGet(setNextBooking(bookings, item)), commentsDtoGet));

        }

        return itemsWithBookings;
    }

    @Override
    public List<ItemDtoGet> search(String text, int from, int size) {
        if (text.isBlank()) {
            return List.of();
        } else {
            List<Item> items = itemRepository.findItemsByText(text, PageRequest.of(from, size)).getContent();
            return items.stream().map(ItemMapper::mapToGetItemDto).collect(Collectors.toList());
        }
    }

    private static final Comparator<Booking> orderByStartDesc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return -1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };

    private static final Comparator<Booking> orderByStartAsc = (a, b) -> {
        if (a.getStart().isAfter(b.getStart())) {
            return 1;
        } else if (a.getStart().isBefore(b.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };

    public static Booking setLastBooking(List<Booking> bookings, Item item) {
        LocalDateTime current = LocalDateTime.now();
        return bookings
                .stream()
                .filter(booking -> booking.getItem().equals(item) && booking.getStart().isBefore(current) &&
                        booking.getStatus().equals(Status.APPROVED))
                .sorted(orderByStartDesc)
                .findFirst()
                .orElse(null);
    }

    public static Booking setNextBooking(List<Booking> bookings, Item item) {
        LocalDateTime current = LocalDateTime.now();
        return bookings
                .stream()
                .filter(booking -> booking.getItem().equals(item) && booking.getStart().isAfter(current) &&
                        booking.getStatus().equals(Status.APPROVED))
                .sorted(orderByStartAsc)
                .findFirst()
                .orElse(null);
    }
}
