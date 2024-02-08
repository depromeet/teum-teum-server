package net.teumteum.alert.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AlertType {

    BEFORE_MEETING("5분 뒤에 모임이 시작돼요!", "모임 장소로 가서 틈틈 모임을 준비해주세요."),
    ;

    private final String title;
    private final String body;
}
