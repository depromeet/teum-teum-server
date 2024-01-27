package net.teumteum.alert.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<UserAlert, Long> {

    List<UserAlert> findAllByUserId(Iterable<Long> userId);
}
