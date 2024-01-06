package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Terms {

    @Column(name = "terms_of_service", nullable = false)
    private Boolean service;

    @Column(name = "privacy_policy", nullable = false)
    private Boolean privacyPolicy;

    @PrePersist
    private void assertTerms() {
        Assert.isTrue(service, () -> "서비스 이용 약관은 항상 동의 되어야 합니다.");
        Assert.isTrue(privacyPolicy, () -> "개인정보 처리 방침은 항상 동의 되어야 합니다.");
    }
}
