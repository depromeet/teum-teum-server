package net.teumteum.alert.domain.response;

import java.time.Instant;
import java.util.List;
import net.teumteum.alert.domain.Alert;

public record AlertsResponse(
    List<AlertResponse> alerts
) {

    public static AlertsResponse of(List<Alert> alerts) {
        return new AlertsResponse(
            alerts.stream()
                .map(alert -> new AlertResponse(
                    alert.getTitle(),
                    alert.getBody(),
                    alert.getType().name(),
                    alert.getCreatedAt(),
                    alert.getIsRead()
                ))
                .toList()
        );
    }

    public record AlertResponse(
        String title,
        String body,
        String type,
        Instant createdAt,
        boolean isRead
    ) {

    }
}
