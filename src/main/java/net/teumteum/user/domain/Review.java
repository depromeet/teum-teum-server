package net.teumteum.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Review {
    별로에요(-1),
    좋아요(1),
    최고에요(2);

    private final int score;
}
