package net.teumteum.alert.domain.response;

import java.util.List;
import net.teumteum.alert.domain.Alert;

public record AlertsResponse(
    List<AlertResponse> alerts
) {

    public static AlertsResponse of(List<Alert> alerts) {
        return new AlertsResponse(
            alerts.stream()
                .map(alert -> new AlertResponse(alert.getTitle(), alert.getBody(), alert.getType().name()))
                .toList()
        );
    }

    public record AlertResponse(
        String title,
        String body,
        String type
    ) {

    }
}
