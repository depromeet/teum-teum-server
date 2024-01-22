package net.teumteum.alert.domain;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;

    public List<Alert> findAllByUserId(Set<Long> userIds) {
        return alertRepository.findAllByUserId(userIds);
    }
}
