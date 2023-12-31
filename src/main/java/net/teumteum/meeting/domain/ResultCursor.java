package net.teumteum.meeting.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultCursor<T> {

    private List<T> data;
    private Boolean hasNext;
    private Long cursorId;

    public static <T> ResultCursor<T> create(List<T> data, Long cursorId, int requestSize) {
        if (hasNextData(data, requestSize)) {
            return new ResultCursor<>(data.subList(0, requestSize), true, cursorId);
        }
        return new ResultCursor<>(data, false, null);
    }

    private static <T> boolean hasNextData(List<T> data, int requestSize) {
        return data.size() == requestSize + 1;
    }
}
