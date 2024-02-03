package net.teumteum.alert.domain;

import java.time.Instant;

public record BeforeMeetingAlert(
    Long userId,
    String token,
    Instant publishedAt
) implements Alertable {

    @Override
    public String title() {
        return "5분 뒤에 모임이 시작돼요!";
    }

    @Override
    public String body() {
        return "모임 장소로 가서 틈틈 모임을 준비해주세요.";
    }

    @Override
    public String type() {
        return "BEFORE_MEETING";
    }
}
