package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByBookerIdOrderByIdDesc(int userId, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerCurrent(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerPast(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start > ?2 and b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerFuture(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerWaiting(int userId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> findByBookerRejected(int userId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 order by b.start desc")
    Page<Booking> findByOwnerAll(int userId, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start <= ?2 and b.end >= ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerCurrent(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start < ?2 and b.end < ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerPast(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.start > ?2 and b.end > ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerFuture(int userId, LocalDateTime currentTime, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerWaiting(int userId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b where b.item.owner.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    Page<Booking> findByOwnerRejected(int userId, Status status, Pageable pageable);

    List<Booking> findByItemIdOrderByIdDesc(int itemId);

}
