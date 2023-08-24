package kr.codesquad.secondhand.exception;

import lombok.Getter;

@Getter
public class SecondHandException extends RuntimeException {

    private final ErrorCode errorCode;

    public SecondHandException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
