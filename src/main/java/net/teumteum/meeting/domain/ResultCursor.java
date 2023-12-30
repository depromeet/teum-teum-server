package net.teumteum.meeting.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ResultCursor<T> {

    private List<T> data;
    private Boolean hasNext;
    private Long cursorId;

}
