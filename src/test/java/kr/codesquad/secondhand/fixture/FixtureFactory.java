package kr.codesquad.secondhand.fixture;

import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;

public class FixtureFactory {

    public static ItemRegisterRequest createItemRegisterRequest() {
        return new ItemRegisterRequest(
                "선풍기",
                10000,
                "바람이 시원한 선풍기",
                "범안 1동",
                "판매중",
                1,
                "가전/잡화"
        );
    }
}
