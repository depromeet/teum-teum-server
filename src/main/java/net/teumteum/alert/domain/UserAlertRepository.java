package net.teumteum.alert.domain;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAlertRepository extends JpaRepository<UserAlert, Long> {

    @Query("select u from user_alert as u where u.userId in :userIds")
    List<UserAlert> findAllByUserId(@Param("userIds") Iterable<Long> userIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from user_alert as u where u.userId = :userId")
    Optional<UserAlert> findByUserIdWithLock(@Param("userId") Long userId);

    Optional<UserAlert> findByUserId(@Param("userId") Long userId);

}
