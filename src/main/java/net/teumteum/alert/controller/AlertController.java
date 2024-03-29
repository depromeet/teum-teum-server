package net.teumteum.alert.controller;

import io.sentry.Sentry;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.UserAlertService;
import net.teumteum.alert.domain.request.RegisterAlertRequest;
import net.teumteum.alert.domain.request.UpdateAlertTokenRequest;
import net.teumteum.alert.domain.response.AlertsResponse;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.core.security.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final UserAlertService userAlertService;
    private final SecurityService securityService;

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.OK)
    public void registerAlert(@Valid @RequestBody RegisterAlertRequest registerAlertRequest) {
        var loginUserId = securityService.getCurrentUserId();
        userAlertService.registerAlert(loginUserId, registerAlertRequest);
    }

    @PatchMapping("/alerts")
    @ResponseStatus(HttpStatus.OK)
    public void updateAlert(@Valid @RequestBody UpdateAlertTokenRequest updateAlertTokenRequest) {
        var loginUserId = securityService.getCurrentUserId();
        userAlertService.updateAlertToken(loginUserId, updateAlertTokenRequest);
    }

    @GetMapping("/alerts")
    @ResponseStatus(HttpStatus.OK)
    public AlertsResponse getAlerts() {
        var loginUserId = securityService.getCurrentUserId();
        return alertService.findAllByUserId(loginUserId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException illegalArgumentException) {
        Sentry.captureException(illegalArgumentException);
        return ErrorResponse.of(illegalArgumentException);
    }
}
