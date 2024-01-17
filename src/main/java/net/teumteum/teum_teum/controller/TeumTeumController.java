package net.teumteum.teum_teum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.sentry.Sentry;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.teumteum.core.error.ErrorResponse;
import net.teumteum.teum_teum.domain.UserAroundLocationsResponse;
import net.teumteum.teum_teum.domain.UserLocationRequest;
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
    public UserAroundLocationsResponse getUserLocation(
        @Valid @RequestBody UserLocationRequest request
    ) throws JsonProcessingException {
        return teumTeumService.processingUserAroundLocations(request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(
        MethodArgumentNotValidException methodArgumentNotValidException) {
        Sentry.captureException(methodArgumentNotValidException);

        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();

        return ErrorResponse.of(errors.get(0).getDefaultMessage());
    }

}
