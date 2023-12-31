package ru.practicum.shareit.request;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByRequesterIdOrderByCreatedDesc(int requesterId);

    Page<Request> findByRequesterIdNotOrderByCreatedDesc(int requesterId, Pageable pageable);


}
