package kr.codesquad.secondhand.exception;

public class DuplicatedException extends SecondHandException {

    public DuplicatedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
