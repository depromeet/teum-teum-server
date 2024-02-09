package net.teumteum.alert.domain;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.response.AlertsResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private static final String EVERY_12AM = "0 0 0 * * *";

    private final AlertRepository alertRepository;

    @Transactional
    public Alert save(Alert alert) {
        return alertRepository.save(alert);
    }

    public AlertsResponse findAllByUserId(Long userId) {
        return AlertsResponse.of(alertRepository.findAllByUserId(userId));
    }

    @Transactional
    @Scheduled(cron = EVERY_12AM)
    public void deleteOneMonthBeforeAlert() {
        var deleteTargets = alertRepository.findAll(Instant.now().minus(1, ChronoUnit.MONTHS));
        alertRepository.deleteAllInBatch(deleteTargets);
    }
}
