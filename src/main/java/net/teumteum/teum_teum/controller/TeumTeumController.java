package net.teumteum.teum_teum.controller;

import io.sentry.Sentry;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.teum_teum.domain.request.UserLocationRequest;
import net.teumteum.teum_teum.domain.response.UserAroundLocationsResponse;
import net.teumteum.teum_teum.service.TeumTeumService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teum-teum")
public class TeumTeumController {

    private final TeumTeumService teumTeumService;

    @PostMapping("/arounds")
    @ResponseStatus(HttpStatus.OK)
    public UserAroundLocationsResponse getUserAroundLocations(
        @Valid @RequestBody UserLocationRequest request) {
        return teumTeumService.saveAndGetUserAroundLocations(request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ErrorResponse handleException(Exception exception) {
        Sentry.captureException(exception);
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            List<ObjectError> errors = bindingResult.getAllErrors();
            return ErrorResponse.of(errors.get(0).getDefaultMessage());
        } else {
            return ErrorResponse.of(exception);
        }
    }
}
