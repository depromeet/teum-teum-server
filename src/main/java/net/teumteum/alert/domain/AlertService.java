package net.teumteum.alert.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Transactional
    public AlertsResponse findAllByUserId(Long userId) {
        var alerts = alertRepository.findAllByUserId(userId);
        var alertsResponse = AlertsResponse.of(alerts);
        alerts.forEach(Alert::read);
        return alertsResponse;
    }

    @Transactional
    @Scheduled(cron = EVERY_12AM)
    public void deleteOneMonthBeforeAlert() {
        var deleteTargets = alertRepository.findAllByCreatedAt(
            LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusMonths(1));
        alertRepository.deleteAllInBatch(deleteTargets);
    }
}
