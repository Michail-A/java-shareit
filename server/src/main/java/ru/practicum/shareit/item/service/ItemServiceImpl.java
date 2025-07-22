package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.CommentAccessException;
import ru.practicum.shareit.error.NotAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDtoAdd;
import ru.practicum.shareit.item.comment.CommentDtoGet;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Override
    public ItemDto add(ItemDtoAdd itemDtoAdd, int userId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("userId=" + userId + " не найден"));
        ItemRequest itemRequest = null;
        if (itemDtoAdd.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDtoAdd.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запрос id= " + itemDtoAdd.getRequestId() + " не найден."));
        }
        Item item = ItemMapper.toNewItem(itemDtoAdd, user, itemRequest);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto get(int id, int userId) {
        userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        ItemDto itemDtoGet = ItemMapper.toItemDto(item);
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());

            Optional<Booking> lastBooking = setLastBooking(bookings, item);
            Optional<Booking> nextBooking = setNextBooking(bookings, item);

            itemDtoGet.setLastBooking(
                    lastBooking.map(booking ->
                            ItemMapper.toItemBookingDtoGet(booking, booking.getEnd())
                    ).orElse(null)
            );

            itemDtoGet.setNextBooking(
                    nextBooking.map(booking ->
                            ItemMapper.toItemBookingDtoGet(booking, booking.getStart())
                    ).orElse(null)
            );
        }
        List<Comment> comments = new ArrayList<>(commentRepository.findAllByItemIdOrderByIdDesc(id));
        List<CommentDtoGet> commentsDtoGet = new ArrayList<>();
        if (!comments.isEmpty()) {
            commentsDtoGet.addAll(comments
                    .stream()
                    .map(ItemMapper::toCommentDtoGet)
                    .toList());

        }
        itemDtoGet.setComments(commentsDtoGet);
        return itemDtoGet;
    }

    @Override
    public ItemDto update(UpdateItemDto updateItemDto, int userId, int itemId) {
        userRepository.findById(userId).orElseThrow(()
                -> new NotFoundException("userId=" + userId + " не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(()
                -> new NotFoundException("itemId=" + itemId + " не найден"));
        if (item.getOwner().getId() != userId) {
            throw new NotAccessException("Ошибка доступа: вещь id={0} не принадлежит userId={1}"
                    .formatted(itemId, userId));
        }
        if (updateItemDto.getName() != null) {
            item.setName(updateItemDto.getName());
        }
        if (updateItemDto.getDescription() != null) {
            item.setDescription(updateItemDto.getDescription());
        }
        if (updateItemDto.getAvailable() != null) {
            item.setAvailable(updateItemDto.getAvailable());
        }
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDtoOwners> getByOwner(int ownerId) {
        userRepository.findById(ownerId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<ItemDtoOwners> itemsWithBookings = new ArrayList<>();
        List<Booking> bookings = bookingRepository.findByOwnerAll(ownerId);
        List<Comment> comments = commentRepository.findByOwnerId(ownerId);

        for (Item item : items) {

            List<CommentDtoGet> commentsDtoGet = new ArrayList<>();

            if (!comments.isEmpty()) {
                commentsDtoGet.addAll(comments
                        .stream()
                        .filter(comment -> comment.getItem().equals(item))
                        .map(ItemMapper::toCommentDtoGet)
                        .toList());
            }

            Optional<Booking> lastBooking = setLastBooking(bookings, item);
            Optional<Booking> nextBooking = setNextBooking(bookings, item);

            LocalDateTime lastEnd = lastBooking.map(Booking::getEnd).orElse(null);
            LocalDateTime nextStart = nextBooking.map(Booking::getStart).orElse(null);

            itemsWithBookings.add(ItemMapper.toItemDtoOwners(
                    item,
                    lastBooking.map(booking -> ItemMapper.toItemBookingDtoGet(booking, lastEnd)).orElse(null),
                    nextBooking.map(value -> ItemMapper.toItemBookingDtoGet(value, nextStart)).orElse(null),
                    commentsDtoGet
            ));

        }
        return itemsWithBookings;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        List<Item> items = itemRepository.search(text);
        return items.stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDtoGet addComment(int itemId, int userId, CommentDtoAdd commentDtoAdd) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь id= " + userId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(itemId);
        if (bookings.stream().anyMatch(booking -> booking.getBooker().equals(user) && booking.getEnd().isBefore(now)
                && booking.getStatus().equals(Status.APPROVED))) {
            Comment comment = ItemMapper.toNewComment(commentDtoAdd, item, user);
            CommentDtoGet commentDtoGet = ItemMapper.toCommentDtoGet(commentRepository.save(comment));
            return commentDtoGet;
        } else {
            throw new CommentAccessException("Пользователь " + userId + "не брал вещь " + itemId + " в аренду");
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

    public static Optional<Booking> setLastBooking(List<Booking> bookings, Item item) {
        LocalDateTime current = LocalDateTime.now();
        return bookings
                .stream()
                .filter(booking -> booking.getItem().equals(item) && booking.getStart().isBefore(current) &&
                        booking.getStatus().equals(Status.APPROVED))
                .sorted(orderByStartDesc)
                .findFirst();
    }

    public static Optional<Booking> setNextBooking(List<Booking> bookings, Item item) {
        LocalDateTime current = LocalDateTime.now();
        return bookings
                .stream()
                .filter(booking -> booking.getItem().equals(item) && booking.getStart().isAfter(current) &&
                        booking.getStatus().equals(Status.APPROVED))
                .sorted(orderByStartAsc)
                .findFirst();
    }

}
