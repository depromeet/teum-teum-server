package net.teumteum.meeting.domain;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MeetingRepository extends JpaRepository<Meeting, Long>, JpaSpecificationExecutor<Meeting> {


    @Query("select m from meeting as m "
        + "where :startPromiseDate <= m.promiseDateTime and m.promiseDateTime < :endPromiseDate")
    List<Meeting> findAlertMeetings(@Param("startPromiseDate") LocalDateTime startPromiseDate,
        @Param("endPromiseDate") LocalDateTime endPromiseDate);
  
    boolean existsById(Long id);
}
