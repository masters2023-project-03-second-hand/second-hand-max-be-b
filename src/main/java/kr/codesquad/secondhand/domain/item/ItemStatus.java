package kr.codesquad.secondhand.domain.item;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemStatus {

    ON_SALE("판매중"), SOLD_OUT("판매완료"), RESERVED("예약중");

    @JsonValue
    private final String status;

    public static ItemStatus fromKorean(String statusName) {
        return Arrays.stream(ItemStatus.values())
                .filter(itemStatus -> itemStatus.getStatus().equals(statusName))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "상품 판매 상태는 (판매중, 판매완료, 예약중) 만 들어올 수 있습니다."));
    }

    public static ItemStatus fromEnglish(String statusName) {
        return Arrays.stream(ItemStatus.values())
                .filter(itemStatus -> itemStatus.name().equalsIgnoreCase(statusName))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        ErrorCode.INVALID_REQUEST,
                        "상품 판매 상태는 (판매중, 판매완료, 예약중) 만 들어올 수 있습니다."));
    }
}
