package net.teumteum.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Oauth {

    @Column(name = "oauth_authenticate_info", unique = true)
    private String oAuthAuthenticateInfo;

    @Column(name = "authenticated")
    private String authenticated;

}
