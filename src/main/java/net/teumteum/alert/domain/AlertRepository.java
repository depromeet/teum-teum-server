package net.teumteum.alert.domain;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findAllByUserId(Long userId);

    @Query("select a from alert as a where a.createdAt <= :createdAt")
    List<Alert> findAll(@Param("createdAt") Instant createdAt);
}
