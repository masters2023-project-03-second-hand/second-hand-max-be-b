package kr.codesquad.secondhand.exception;

public class NotFoundException extends SecondHandException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
