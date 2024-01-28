package net.teumteum.alert.domain.request;

import jakarta.validation.constraints.NotNull;

public record RegisterAlertRequest(
    @NotNull
    String token
) {

}
