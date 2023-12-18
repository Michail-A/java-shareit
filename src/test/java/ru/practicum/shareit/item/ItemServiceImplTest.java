package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.ItemBookingDtoGet;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    @InjectMocks
    private ItemServiceImpl itemService;

    Item item;
    User user;
    Booking booking;
    CommentDtoAdd commentDtoAdd;
    Comment comment;
    ItemDtoAdd itemDtoAdd;
    Request request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test");
        user.setEmail("test@test.com");

        item = new Item();
        item.setId(1);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDtoAdd = new ItemDtoAdd();
        itemDtoAdd.setName(item.getName());
        itemDtoAdd.setDescription(item.getDescription());
        itemDtoAdd.setAvailable(item.isAvailable());

        commentDtoAdd = new CommentDtoAdd();
        commentDtoAdd.setText("Test comment");

        comment = new Comment();
        comment.setItem(item);
        comment.setId(1);
        comment.setText(commentDtoAdd.getText());
        comment.setAuthor(user);

        booking = new Booking();
        booking.setId(1);
        booking.setStart(LocalDateTime.now().minusMonths(1));
        booking.setEnd(LocalDateTime.now().minusDays(7));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.APPROVED);

        request = new Request();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        request.setDescription("Test");
        request.setId(1);
    }

    @Test
    void addComment() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDtoGet newComment = itemService.addComment(item.getId(), user.getId(), commentDtoAdd);

        assertEquals(newComment, ItemMapper.mapToCommentDtoGet(comment));
    }

    @Test
    void addCommentFailUser() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.addComment(item.getId(), user.getId(), commentDtoAdd));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addCommentFailBooking() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of());

        final CommentAccessError e = assertThrows(CommentAccessError.class,
                () -> itemService.addComment(item.getId(), user.getId(), commentDtoAdd));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void create() {
        itemDtoAdd.setRequestId(1);
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.ofNullable(request));

        ItemDtoGet newItem = itemService.create(itemDtoAdd, user.getId());

        assertEquals(newItem, ItemMapper.mapToGetItemDtoBooking(item, new ItemBookingDtoGet(),
                new ItemBookingDtoGet(), List.of()));
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void createFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(itemDtoAdd, 1));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void createFailRequestNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        itemDtoAdd.setRequestId(10);
        when(requestRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(itemDtoAdd, 1));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        itemDtoAdd.setName("Update name");
        Item updateItem = new Item();
        updateItem.setId(item.getId());
        updateItem.setName(itemDtoAdd.getName());
        updateItem.setDescription(item.getDescription());
        updateItem.setAvailable(item.isAvailable());
        updateItem.setOwner(item.getOwner());
        when(itemRepository.saveAndFlush(any(Item.class))).thenReturn(updateItem);

        ItemDtoGet newItem = itemService.update(itemDtoAdd, user.getId(), item.getId());

        assertEquals(newItem, ItemMapper.mapToGetItemDto(updateItem));
        verify(itemRepository).saveAndFlush(any(Item.class));
    }

    @Test
    void updateFailUserNotFound() {
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDtoAdd, user.getId(), item.getId()));
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    void updateFailOwner() {
        item.setOwner(new User());
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(itemDtoAdd, user.getId(), item.getId()));
        verify(itemRepository, never()).saveAndFlush(any(Item.class));
    }

    @Test
    void get() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.findByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of());
        when(commentRepository.findAllByItemIdOrderByIdDesc(anyInt())).thenReturn(List.of(comment));

        ItemDtoGet getItwm = itemService.get(item.getId(), user.getId());

        assertEquals(getItwm, ItemMapper.mapToGetItemDtoBooking(item, null,
                null, List.of(ItemMapper.mapToCommentDtoGet(comment))));
    }

    @Test
    void getFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.get(item.getId(), user.getId()));
    }

    @Test
    void getByUser() {

        Page<Item> items = new PageImpl<>(List.of(item));
        Page<Booking> bookings = new PageImpl<>(List.of());
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        when(bookingRepository.findByOwnerAll(anyInt(), any(Pageable.class))).thenReturn(bookings);
        when(commentRepository.findByOwnerId(anyInt())).thenReturn(new ArrayList<>());

        List<ItemDtoGet> getItems = itemService.getByUser(user.getId(), 0, 10);
        assertEquals(getItems.get(0).getName(), items.getContent().get(0).getName());
    }

    @Test
    void getByUserFailUserNotFound() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        final NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getByUser(user.getId(), 0, 10));
    }

    @Test
    void search() {
        Page<Item> items = new PageImpl<>(List.of(item));
        when(itemRepository.findItemsByText(any(), any(Pageable.class))).thenReturn(items);

        List<ItemDtoGet> getSearch = itemService.search("Test", 0, 10);
        assertEquals(getSearch.get(0).getName(), items.getContent().get(0).getName());
    }


    @Test
    void comparatorTest() {
        Booking lastBooking = new Booking();
        lastBooking.setId(2);
        lastBooking.setStart(LocalDateTime.now().minusDays(4));
        lastBooking.setEnd(LocalDateTime.now().minusDays(2));
        lastBooking.setItem(item);
        lastBooking.setBooker(new User());
        lastBooking.setStatus(Status.APPROVED);

        Booking nextBooking = new Booking();
        nextBooking.setId(2);
        nextBooking.setStart(LocalDateTime.now().plusDays(2));
        nextBooking.setEnd(LocalDateTime.now().plusDays(5));
        nextBooking.setItem(item);
        nextBooking.setBooker(new User());
        nextBooking.setStatus(Status.APPROVED);

        Page<Item> items = new PageImpl<>(List.of(item));
        Page<Booking> bookings = new PageImpl<>(List.of(lastBooking, nextBooking));
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        when(bookingRepository.findByOwnerAll(anyInt(), any(Pageable.class))).thenReturn(bookings);
        when(commentRepository.findByOwnerId(anyInt())).thenReturn(new ArrayList<>());

        List<ItemDtoGet> getItems = itemService.getByUser(user.getId(), 0, 10);

        assertEquals(getItems.get(0).getLastBooking(), BookingMapper.mapToItemBookingDtoGet(lastBooking));
        assertEquals(getItems.get(0).getNextBooking(), BookingMapper.mapToItemBookingDtoGet(nextBooking));
    }

    @Test
    void getByUserCommentsAdd() {
        Page<Item> items = new PageImpl<>(List.of(item));
        Page<Booking> bookings = new PageImpl<>(List.of());
        when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findAllByOwnerId(anyInt(), any(Pageable.class))).thenReturn(items);
        when(bookingRepository.findByOwnerAll(anyInt(), any(Pageable.class))).thenReturn(bookings);
        when(commentRepository.findByOwnerId(anyInt())).thenReturn(List.of(comment));

        List<ItemDtoGet> getItems = itemService.getByUser(user.getId(), 0, 10);

        assertEquals(getItems.get(0).getComments().get(0), ItemMapper.mapToCommentDtoGet(comment));
    }

    @Test
    void serchEmpty() {
        List<ItemDtoGet> items = itemService.search(" ", 0, 10);
        assertEquals(items.size(), 0);
        verify(itemRepository, never()).findItemsByText(anyString(), any(Pageable.class));
    }
}