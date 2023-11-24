package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerIdOrderByIdDesc(int userId);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start <= ?2 and b.end >= ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerCurrent(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start < ?2 and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerPast(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.start > ?2 and b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerFuture(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerWaiting(int userId, Status status);

    @Query(value = "select b from Booking b where b.booker.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByBookerRejected(int userId, Status status);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 order by b.start desc")
    List<Booking> findByOwnerAll(int userId);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 and b.start <= ?2 and b.end >= ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerCurrent(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 and b.start < ?2 and b.end < ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerPast(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 and b.start > ?2 and b.end > ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerFuture(int userId, LocalDateTime currentTime);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerWaiting(int userId, Status status);

    @Query(value = "select b from Booking b inner join Item as i on b.item.id = i.id " +
            "inner join User u on i.owner.id = u.id where u.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerRejected(int userId, Status status);

    List<Booking> findByItemIdOrderByIdDesc(int itemId);
}
