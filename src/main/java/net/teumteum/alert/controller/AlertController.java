package net.teumteum.alert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.teumteum.alert.domain.AlertService;
import net.teumteum.alert.domain.request.RegisterAlertRequest;
import net.teumteum.core.security.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final SecurityService securityService;

    @PostMapping("/alerts")
    @ResponseStatus(HttpStatus.OK)
    public void registerAlert(@Valid @RequestBody RegisterAlertRequest registerAlertRequest) {
        var loginUserId = securityService.getCurrentUserId();
        alertService.registerAlert(loginUserId, registerAlertRequest);
    }
}
