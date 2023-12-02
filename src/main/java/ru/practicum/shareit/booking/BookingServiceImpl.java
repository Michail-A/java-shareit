package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoGet;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.exception.*;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    @Override
    public Booking create(BookingDto bookingDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart())) {
            throw new DateException("Дата окончания не может быть раньше или равна дате начала");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Вещь не найдена")
        );

        if (!item.isAvailable()) {
            throw new ItemIsNotAvailableException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId() == userId) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        //Проверка на доступность дат бронирования
        List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());
        for (Booking booking : bookings) {
            if ((booking.getStart().isAfter(bookingDto.getStart()) && booking.getStart().isBefore(bookingDto.getEnd())) ||
                    (booking.getEnd().isAfter(bookingDto.getStart()) && booking.getEnd().isBefore(bookingDto.getEnd())) ||
                    (bookingDto.getStart().isAfter(booking.getStart()) && bookingDto.getEnd().isBefore(booking.getEnd())) ||
                    (bookingDto.getStart().isBefore(booking.getStart()) && bookingDto.getEnd().isAfter(booking.getEnd()))) {
                throw new ItemIsNotAvailableException("Даты бронирования заняты");
            }
        }


        Booking booking = BookingMapper.mapToNewBooking(bookingDto, user, item);
        return bookingRepository.save(booking);
    }

    @Override
    public BookingDtoGet setApprove(int userId, int bookingId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundBookingException("Бронирование id=" + bookingId + " не найдено"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundBookingException("Бронирование пользователя id = " + booking.getItem().getOwner().getId()
                    + "не найдено");
        }

        Status status;

        if (approve) {
            if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
                throw new ItemIsNotAvailableException("Бронирование уже подтверждено или отклонено");
            }
            status = Status.APPROVED;
        } else {
            status = Status.REJECTED;
        }

        booking.setStatus(status);

        return BookingMapper.mapToApprove(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoGet getForOwnerOrBooker(int bookingId, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundBookingException("Бронирование id=" + bookingId + " не найдено"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundBookingException("Ошибка доступа. Бронирование может посмотреть только владелец" +
                    " вещи или создатель");
        }
        return BookingMapper.mapToApprove(booking);
    }

    @Override
    public List<BookingDtoGet> getForUser(String stateText, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));

        State state = State.mapFromText(stateText);

        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerIdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(userId, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerWaiting(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerRejected(userId, Status.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::mapToApprove)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoGet> getBookingsForOwner(String stateText, int userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь не найден"));

        State state = State.mapFromText(stateText);

        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerAll(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerCurrent(userId, currentTime);
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerPast(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository.findByOwnerFuture(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerWaiting(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerRejected(userId, Status.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::mapToApprove)
                .collect(Collectors.toList());
    }
}

