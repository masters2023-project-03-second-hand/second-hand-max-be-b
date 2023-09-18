package kr.codesquad.secondhand.fixture;

import java.util.ArrayList;
import java.util.List;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.chat.WhoIsLast;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.presentation.dto.item.ItemRegisterRequest;
import kr.codesquad.secondhand.presentation.dto.item.ItemUpdateRequest;

public class FixtureFactory {

    public static ItemRegisterRequest createItemRegisterRequest() {
        return new ItemRegisterRequest(
                "선풍기",
                10000L,
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
                .price(10000L)
                .categoryName(categoryName)
                .member(member)
                .thumbnailUrl("url")
                .tradingRegion("범박동")
                .build();
    }

    public static Item createItem(String title, String categoryName, Member member, ItemStatus status) {
        return Item.builder()
                .title(title)
                .status(status)
                .price(10000L)
                .categoryName(categoryName)
                .member(member)
                .thumbnailUrl("url")
                .tradingRegion("범박동")
                .build();
    }

    public static Item createDefaultRegionItem(String title, String categoryName, Member member) {
        return Item.builder()
                .title(title)
                .status(ItemStatus.ON_SALE)
                .price(10000L)
                .categoryName(categoryName)
                .member(member)
                .thumbnailUrl("url")
                .tradingRegion("역삼1동")
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
                10000L,
                "바람이 시원한 선풍기",
                "범안 1동",
                "판매중",
                1,
                "가전/잡화",
                List.of("url1", "url2")
        );
    }

    public static List<Member> createPartner() {
        List<Member> members = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            members.add(
                    Member.builder()
                            .email(i + "test@secondhand.com")
                            .loginId(i + "testId")
                            .profileUrl("image-url")
                            .build()
            );
        }
        return members;
    }

    public static List<ChatRoom> createChatRooms(Member member, List<Member> partners, Item item) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        int tmp = partners.size() / 2;
        for (int i = 0; i < tmp; i++) {
            chatRooms.add(
                    ChatRoom.builder()
                            .subject(i + 1 + "번 채팅방")
                            .status(WhoIsLast.FROM)
                            .sender(partners.get(i))
                            .receiver(member)
                            .item(item)
                            .build()
            );
        }
        for (int i = tmp; i < partners.size(); i++) {
            chatRooms.add(
                    ChatRoom.builder()
                            .subject(i + 1 + "번 채팅방")
                            .status(WhoIsLast.FROM)
                            .sender(member)
                            .receiver(partners.get(i))
                            .item(item)
                            .build()
            );
        }
        return chatRooms;
    }
}
