package kr.codesquad.secondhand.presentation.support.converter;

import java.util.Arrays;
import kr.codesquad.secondhand.application.wishitem.WishItemService;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IsWish {

    YES("yes") {
        @Override
        public void invoke(WishItemService wishItemService, Long itemId, Long memberId) {
            wishItemService.registerWishItem(itemId, memberId);
        }
    }, NO("no") {
        @Override
        public void invoke(WishItemService wishItemService, Long itemId, Long memberId) {
            wishItemService.removeWishItem(itemId, memberId);
        }
    };

    private final String status;

    public static IsWish of(String isWish) {
        return Arrays.stream(IsWish.values())
                .filter(wish -> isWish != null && wish.getStatus().equals(isWish.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_PARAMETER,
                        "관심상품 등록의 파라미터는 yes, no 중 하나만 들어올 수 있습니다."));
    }

    public abstract void invoke(WishItemService wishItemService, Long itemId, Long memberId);
}
