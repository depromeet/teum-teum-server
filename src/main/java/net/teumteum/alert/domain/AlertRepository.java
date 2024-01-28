package net.teumteum.alert.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<UserAlert, Long> {

    @Query("select u from user_alert as u where u.userId in :userIds")
    List<UserAlert> findAllByUserId(@Param("userIds") Iterable<Long> userIds);
}
