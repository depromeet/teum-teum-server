package net.teumteum.core.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String message;

    public static ErrorResponse of(Throwable exception) {
        return new ErrorResponse(exception.getMessage());
    }

    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }
}
