package kr.codesquad.secondhand.fixture;

import java.util.List;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;

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

    public static Item createItem(String title, String categoryName, Member member) {
        return Item.builder()
                .title(title)
                .status(ItemStatus.ON_SALE)
                .price(10000)
                .categoryName(categoryName)
                .member(member)
                .thumbnailUrl("url")
                .tradingRegion("범박동")
                .build();
    }

    public static Member createMember() {
        return Member.builder()
                .email("23Yong@secondhand.com")
                .loginId("23Yong")
                .profileUrl("image-url")
                .build();
    }

    public static ItemUpdateRequest createItemUpdateRequest() {
        return new ItemUpdateRequest(
                "수정제목",
                10000,
                "바람이 시원한 선풍기",
                "범안 1동",
                "판매중",
                1,
                "가전/잡화",
                List.of("url1", "url2")
        );
    }
}
