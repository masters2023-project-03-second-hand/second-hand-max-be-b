package kr.codesquad.secondhand.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {

    private final int statusCode;
    private final String message;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
