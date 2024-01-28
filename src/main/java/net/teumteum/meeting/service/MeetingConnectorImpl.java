package net.teumteum.meeting.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.teumteum.meeting.domain.Meeting;
import net.teumteum.meeting.domain.MeetingConnector;
import net.teumteum.meeting.domain.MeetingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingConnectorImpl implements MeetingConnector {

    private final MeetingRepository meetingRepository;

    @Override
    public Optional<Meeting> findById(Long id) {
        return meetingRepository.findById(id);
    }

    @Override
    public boolean existById(Long id) {
        return meetingRepository.existsById(id);
    }
}
