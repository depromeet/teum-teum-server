package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.teumteum.core.security.Authenticated;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class OAuth {

    @Column(name = "oauth_id", unique = true, nullable = false)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(name = "authenticated", nullable = false)
    private Authenticated authenticated;
}
