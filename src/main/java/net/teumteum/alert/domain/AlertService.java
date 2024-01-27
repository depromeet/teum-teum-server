package net.teumteum.alert.domain;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.request.RegisterAlertRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public void registerAlert(Long userId, RegisterAlertRequest registerAlertRequest) {
        var alert = new UserAlert(null, userId, registerAlertRequest.token());
        alertRepository.save(alert);
    }

    public List<UserAlert> findAllByUserId(Set<Long> userIds) {
        return alertRepository.findAllByUserId(userIds);
    }
}
