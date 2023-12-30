package net.teumteum.meeting.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Page<Meeting> findByPromiseDateTimeGreaterThanOrderByIdDesc(Pageable size, LocalDateTime promiseDateTime);

    Page<Meeting> findByIdLessThanEqualAndPromiseDateTimeGreaterThanOrderByIdDesc(Long id, Pageable size, LocalDateTime promiseDateTime);

}
