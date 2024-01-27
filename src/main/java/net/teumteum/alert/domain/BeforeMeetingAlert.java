package net.teumteum.alert.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Instant;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BeforeMeetingAlert implements Alertable {

    private final Long userId;
    @JsonIgnore
    private final String token;
    private final Instant publishedAt = Instant.now();

    @Override
    public String token() {
        return null;
    }
}
