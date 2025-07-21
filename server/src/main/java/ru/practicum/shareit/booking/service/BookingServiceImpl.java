package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.DateException;
import ru.practicum.shareit.error.ItemIsNotAvailableException;
import ru.practicum.shareit.error.NotAccessException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @Override
    public BookingDto add(BookingDtoAdd bookingDtoAdd, int bookerId) {
        User user = userRepository.findById(bookerId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        if (bookingDtoAdd.getEnd().isBefore(bookingDtoAdd.getStart()) ||
                bookingDtoAdd.getEnd().isEqual(bookingDtoAdd.getStart())) {
            throw new DateException("Дата окончания не может быть раньше или равна дате начала");
        }

        Item item = itemRepository.findById(bookingDtoAdd.getItemId()).orElseThrow(
                () -> new NotFoundException("Вещь не найдена")
        );

        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Вещь не доступна для бронирования");
        }

        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException("Нельзя забронировать свою вещь");
        }

        List<Booking> bookings = bookingRepository.findByItemIdOrderByIdDesc(item.getId());
        for (Booking booking : bookings) {
            if ((booking.getStart().isAfter(bookingDtoAdd.getStart()) && booking.getStart().isBefore(bookingDtoAdd.getEnd())) ||
                    (booking.getEnd().isAfter(bookingDtoAdd.getStart()) && booking.getEnd().isBefore(bookingDtoAdd.getEnd())) ||
                    (bookingDtoAdd.getStart().isAfter(booking.getStart()) && bookingDtoAdd.getEnd().isBefore(booking.getEnd())) ||
                    (bookingDtoAdd.getStart().isBefore(booking.getStart()) && bookingDtoAdd.getEnd().isAfter(booking.getEnd()))) {
                throw new ItemIsNotAvailableException("Даты бронирования заняты");
            }
        }


        Booking booking = BookingMapper.toNewBooking(bookingDtoAdd, item, user);
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setApprove(int userId, int bookingId, Boolean approve) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование id=" + bookingId + " не найдено"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotAccessException("Бронирование пользователя id = " + booking.getItem().getOwner().getId()
                    + " не найдено");
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

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto get(int userId, int bookingId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование id=" + bookingId + " не найдено"));

        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("Ошибка доступа. Бронирование может посмотреть только владелец" +
                    " вещи или создатель");
        }
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getForUser(int userId, String stateText) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        State state = State.mapFromText(stateText);

        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository
                    .findByBookerIdOrderByIdDesc(userId);
            case CURRENT -> bookingRepository
                    .findByBookerCurrent(userId, currentTime);
            case PAST -> bookingRepository
                    .findByBookerPast(userId, currentTime);
            case FUTURE -> bookingRepository
                    .findByBookerFuture(userId, currentTime);
            case WAITING -> bookingRepository
                    .findByBookerWaiting(userId, Status.WAITING);
            case REJECTED -> bookingRepository
                    .findByBookerRejected(userId, Status.REJECTED);
            default -> Collections.emptyList();
        };

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getForOwner(int userId, String stateText) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"));

        State state = State.mapFromText(stateText);

        LocalDateTime currentTime = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .findByOwnerAll(userId);
                break;
            case CURRENT:
                bookings = bookingRepository
                        .findByOwnerCurrent(userId, currentTime);
                break;
            case PAST:
                bookings = bookingRepository
                        .findByOwnerPast(userId, currentTime);
                break;
            case FUTURE:
                bookings = bookingRepository
                        .findByOwnerFuture(userId, currentTime);
                break;
            case WAITING:
                bookings = bookingRepository
                        .findByOwnerWaiting(userId, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository
                        .findByOwnerRejected(userId, Status.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }

        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
