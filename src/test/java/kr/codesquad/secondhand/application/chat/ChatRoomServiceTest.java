package kr.codesquad.secondhand.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("비즈니스 로직 - 채팅방")
public class ChatRoomServiceTest extends ApplicationTestSupport {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatLogService chatLogService;


    @DisplayName("채팅방 전체 목록을 조회할 떄")
    @Nested
    class Read {

        @DisplayName("첫 페이지에서 최근 전송된 채팅 순으로 보여진다.")
        @Test
        void givenChatRooms_whenFirstPage_thenSuccess() {
            // given
            Member member = signup();
            List<Member> partners = getPartners();
            Item item = supportRepository.save(FixtureFactory.createItem("item", "가전/잡화", member));
            supportRepository.save(FixtureFactory.createChatRooms(member, partners, item));
            Pageable pageable = PageRequest.of(0, 10);

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(1L, pageable, null);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(1L),
                    () -> assertThat(response.getContents().get(0).getChatPartnerName()).isEqualTo("30testId"),
                    () -> assertThat(response.getContents().get(0).getLastSendMessage()).isEqualTo("30번 채팅방"),
                    () -> assertThat(response.getContents().get(9).getChatPartnerName()).isEqualTo("21testId")
            );
        }

        @DisplayName("마지막 페이지에서 최근 전송된 채팅 순으로 보여진다.")
        @Test
        void givenChatRooms_whenLastPage_thenSuccess() {
            // given
            Member member = signup();
            List<Member> partners = getPartners();
            Item item = supportRepository.save(FixtureFactory.createItem("item", "가전/잡화", member));
            supportRepository.save(FixtureFactory.createChatRooms(member, partners, item));
            Pageable pageable = PageRequest.of(2, 10);

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(1L, pageable, null);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull(),
                    () -> assertThat(response.getContents().get(0).getChatPartnerName()).isEqualTo("10testId"),
                    () -> assertThat(response.getContents().get(9).getChatPartnerName()).isEqualTo("1testId")
            );
        }

        @DisplayName("사용자가 읽지 않은 메시지 개수를 확인할 수 있다.")
        @Test
        void givenSender_whenCountNewMessage_thenSuccess() {
            // given
            Member sender = signup();
            Member receiver = supportRepository.save(Member.builder()
                    .email("he2joo@secondhand.com")
                    .loginId("joy")
                    .profileUrl("image-url")
                    .build());
            Item item = supportRepository.save(FixtureFactory.createItem("구매하는 상품", "가전/잡화", receiver));

            ChatRoom chatRoom = supportRepository.save(ChatRoom.builder()
                    .item(item)
                    .subject("")
                    .seller(receiver)
                    .buyer(sender)
                    .build());

            chatLogService.sendMessage("선풍기 사려 그러는데요!", chatRoom.getId(), sender.getId());
            chatLogService.sendMessage("혹시 할인 되나요..?", chatRoom.getId(), sender.getId());

            Pageable pageable = PageRequest.of(0, 10);

            // when
            CustomSlice<ChatRoomResponse> senderResponse = chatRoomService.read(1L, pageable, null);
            CustomSlice<ChatRoomResponse> receiverResponse = chatRoomService.read(2L, pageable, null);

            // then
            assertThat(senderResponse.getContents().get(0).getNewMessageCount()).isEqualTo(0);
            assertThat(receiverResponse.getContents().get(0).getNewMessageCount()).isEqualTo(2);
        }

        private Member signup() {
            return supportRepository.save(FixtureFactory.createMember());
        }

        private List<Member> getPartners() {
            return supportRepository.save(FixtureFactory.createPartner());
        }
    }

    @DisplayName("채팅방을 생성할 때")
    @Nested
    class Create {

        @DisplayName("채팅방 생성에 성공한다.")
        @Test
        void given_whenCreateChatRoom_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전", member));

            // when
            Long chatRoomId = chatRoomService.createChatRoom(item.getId(), member.getId());

            // then
            Item foundItem = supportRepository.findById(Item.class, item.getId()).get();

            assertAll(
                    () -> assertThat(chatRoomId).isNotNull(),
                    () -> assertThat(foundItem.getChatCount()).isEqualTo(1)
            );
        }
    }

    @DisplayName("상품별 채팅방 목록 조회에 성공한다.")
    @Test
    void given_whenReadChatByItem_thenSuccess() {
        // given
        Member seller = supportRepository.save(FixtureFactory.createMember());
        Member buyer = supportRepository.save(Member.builder()
                        .loginId("joy")
                        .email("joy@codesquad.com")
                        .profileUrl("image.png")
                        .build());

        Item searchedItem = supportRepository.save(FixtureFactory.createItem("searchedItem", "가전/잡화", seller));
        Item item = supportRepository.save(FixtureFactory.createItem("item", "기타", seller));
        for (int i = 0; i < 10; i++) {
            supportRepository.save(ChatRoom.builder()
                    .seller(seller)
                    .buyer(buyer)
                    .item(searchedItem)
                    .subject("searchedItem")
                    .build());
        }
        for (int i = 0; i < 10; i++) {
            supportRepository.save(ChatRoom.builder()
                    .seller(seller)
                    .buyer(buyer)
                    .item(item)
                    .subject("item")
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 10);

        // when
        CustomSlice<ChatRoomResponse> response = chatRoomService.read(1L, pageable, searchedItem.getId());

        // then
        assertAll(
                () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                () -> assertThat(response.getPaging().getNextCursor()).isNull(),
                () -> assertThat(response.getContents().size()).isEqualTo(10),
                () -> assertThat(response.getContents().get(0).getLastSendMessage()).isEqualTo("searchedItem"),
                () -> assertThat(response.getContents().get(9).getLastSendMessage()).isEqualTo("searchedItem")
        );
    }
}
