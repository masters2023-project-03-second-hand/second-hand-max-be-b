package kr.codesquad.secondhand.exception;

public class InternalServerException extends SecondHandException {

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
