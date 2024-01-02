package net.teumteum.meeting.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class PageDto<T> {

    private T data;
    private Boolean hasNext;

    public static <T> PageDto<T> of(T data, boolean hasNext) {
        return new PageDto<>(data, hasNext);
    }
}
