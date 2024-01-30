package net.teumteum.alert.domain;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.request.RegisterAlertRequest;
import net.teumteum.alert.domain.request.UpdateAlertTokenRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public void registerAlert(Long userId, RegisterAlertRequest registerAlertRequest) {
        alertRepository.findByUserId(userId)
            .ifPresentOrElse(userAlert -> {
                throw new IllegalArgumentException("이미 토큰이 생성된 user입니다. \"" + userId +"\"");
            }, () -> {
                var alert = new UserAlert(null, userId, registerAlertRequest.token());
                alertRepository.save(alert);
            });
    }

    @Transactional
    public void updateAlertToken(Long userId, UpdateAlertTokenRequest updateAlertTokenRequest) {
        var userAlert = alertRepository.findByUserIdWithLock(userId)
            .orElseThrow(() -> new IllegalArgumentException("userId에 해당하는 토큰을 찾을 수 없습니다."));

        userAlert.updateToken(updateAlertTokenRequest.token());
    }

    public List<UserAlert> findAllByUserId(Set<Long> userIds) {
        return alertRepository.findAllByUserId(userIds);
    }
}
