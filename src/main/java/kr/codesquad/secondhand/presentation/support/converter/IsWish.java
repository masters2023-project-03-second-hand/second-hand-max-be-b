package kr.codesquad.secondhand.presentation.support.converter;

import java.util.Arrays;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;

public enum IsWish {

    YES,
    NO;

    public static IsWish from(String isWish) {
        return Arrays.stream(IsWish.values())
                .filter(wish -> wish.name().equalsIgnoreCase(isWish))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_PARAMETER,
                        "관심상품 등록의 파라미터는 yes, no 중 하나만 들어올 수 있습니다."));
    }
}
