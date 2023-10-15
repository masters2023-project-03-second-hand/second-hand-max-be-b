package kr.codesquad.secondhand.exception;

public class NotFoundException extends SecondHandException {

    private static final String ITEM_NOT_FOUND_MESSAGE_FORMAT = "%s 번호의 아이템을 찾을 수 없습니다.";
    private static final String REGION_NOT_FOUND_MESSAGE_FORMAT = "아이디 %s를 가진 지역을 찾을 수 없습니다.";

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public static NotFoundException itemNotFound(ErrorCode errorCode, Long itemId) {
        return new NotFoundException(errorCode, String.format(ITEM_NOT_FOUND_MESSAGE_FORMAT, itemId));
    }

    public static NotFoundException regionNotFound(ErrorCode errorCode, Long addressId) {
        return new NotFoundException(errorCode, String.format(REGION_NOT_FOUND_MESSAGE_FORMAT, addressId));
    }
}
