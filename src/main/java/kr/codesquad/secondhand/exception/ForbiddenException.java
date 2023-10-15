package kr.codesquad.secondhand.exception;

public class ForbiddenException extends SecondHandException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
