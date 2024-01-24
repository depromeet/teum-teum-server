package net.teumteum.meeting.domain;

import java.util.Optional;

public interface MeetingConnector {
    Optional<Meeting> findById(Long id);

    boolean existById(Long id);
}
