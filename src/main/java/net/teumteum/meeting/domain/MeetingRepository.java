package net.teumteum.meeting.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    List<Meeting> findByPromiseDateTimeGreaterThanOrderByIdDesc(Pageable size, LocalDateTime promiseDateTime);

    List<Meeting> findByIdLessThanEqualAndPromiseDateTimeGreaterThanOrderByIdDesc(Long id, Pageable size, LocalDateTime promiseDateTime);

}
