package kr.codesquad.secondhand.exception;

public class UnAuthorizedException extends SecondHandException {

    public UnAuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnAuthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
