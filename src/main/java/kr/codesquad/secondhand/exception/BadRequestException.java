package kr.codesquad.secondhand.exception;

public class BadRequestException extends SecondHandException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
