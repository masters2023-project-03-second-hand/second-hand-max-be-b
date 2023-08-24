package kr.codesquad.secondhand.exception;

import lombok.Getter;

@Getter
public class SecondHandException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String message;

    public SecondHandException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }

    public SecondHandException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
