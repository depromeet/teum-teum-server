package net.teumteum.alert.app;

import static net.teumteum.alert.app.AlertExecutorConfigurer.ALERT_EXECUTOR;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.Alert;
import net.teumteum.alert.domain.AlertPublisher;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.AlertType;
import net.teumteum.alert.domain.UserAlertService;
import net.teumteum.meeting.domain.BeforeMeetingAlerted;
import net.teumteum.meeting.domain.EndMeetingAlerted;
import net.teumteum.user.UserRecommended;
import net.teumteum.user.domain.UserDeletedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class AlertHandler {

    private final UserAlertService userAlertService;
    private final AlertService alertService;
    private final AlertPublisher alertPublisher;

    @EventListener(UserDeletedEvent.class)
    public void handleUserDeleteEvent(UserDeletedEvent userDeletedEvent) {
        userAlertService.deleteAlertByUserId(userDeletedEvent.id());
    }

    @Async(ALERT_EXECUTOR)
    @EventListener(BeforeMeetingAlerted.class)
    public void handleBeforeMeetingAlerts(BeforeMeetingAlerted alerted) {
        userAlertService.findAllByUserId(alerted.userIds())
            .stream()
            .map(userAlert -> Pair.of(userAlert.getToken(),
                new Alert(null, userAlert.getUserId(), "5분 뒤에 모임이 시작돼요!",
                    "모임 장소로 가서 틈틈 모임을 준비해주세요.", AlertType.BEFORE_MEETING, true)))
            .map(tokenAndAlert -> Pair.of(tokenAndAlert.getFirst(), alertService.save(tokenAndAlert.getSecond())))
            .forEach(
                tokenAndAlert -> alertPublisher.publish(tokenAndAlert.getFirst(), tokenAndAlert.getSecond(), Map.of())
            );
    }

    @Async(ALERT_EXECUTOR)
    @EventListener(EndMeetingAlerted.class)
    public void handleStartMeetingAlerts(EndMeetingAlerted alerted) {
        userAlertService.findAllByUserId(alerted.userIds())
            .stream()
            .map(userAlert -> Pair.of(userAlert.getToken(),
                new Alert(null, userAlert.getUserId(), alerted.meetingTitle(),
                    "모임이 종료되었어요", AlertType.END_MEETING, true)))
            .map(tokenAndAlert -> Pair.of(tokenAndAlert.getFirst(), alertService.save(tokenAndAlert.getSecond())))
            .forEach(tokenAndAlert ->
                alertPublisher.publish(tokenAndAlert.getFirst(), tokenAndAlert.getSecond(),
                    Map.of("meetingId", alerted.meetingId().toString(), "participants",
                        toCommaString(alerted.userIds().stream().toList())))
            );
    }

    private String toCommaString(List<Long> ids) {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.size() - 1; i++) {
            stringBuilder.append(ids.get(i)).append(",");
        }
        stringBuilder.append(ids.getLast());
        return stringBuilder.toString();
    }

    @Async(ALERT_EXECUTOR)
    @EventListener(UserRecommended.class)
    public void handleUserRecommended(UserRecommended alerted) {
        userAlertService.findAllByUserId(Set.of(alerted.userId()))
            .stream()
            .map(userAlert -> Pair.of(userAlert.getToken(),
                new Alert(null, userAlert.getUserId(), "틈 채우기",
                    alerted.recommenderName() + "님이 당신을 추천했어요!", AlertType.RECOMMEND_USER, true)))
            .map(tokenAndAlert -> Pair.of(tokenAndAlert.getFirst(), alertService.save(tokenAndAlert.getSecond())))
            .forEach(tokenAndAlert ->
                alertPublisher.publish(tokenAndAlert.getFirst(), tokenAndAlert.getSecond(), Map.of())
            );
    }
}
