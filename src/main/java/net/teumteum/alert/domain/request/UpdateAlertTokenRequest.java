package net.teumteum.alert.domain.request;

import jakarta.validation.constraints.NotNull;

public record UpdateAlertTokenRequest(
    @NotNull
    String token
) {

}
