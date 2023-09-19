package kr.codesquad.secondhand.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.chat.ChatRoomResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationTest
public class ChatRoomServiceTest extends ApplicationTestSupport {

    @Autowired
    private ChatRoomService chatRoomService;

    @DisplayName("채팅 전체 목록을 조회할 떄")
    @Nested
    class Read {

        @DisplayName("첫 페이지에서 최근 전송된 채팅 순으로 보여진다.")
        @Test
        void givenChatsData_whenFirstPage_thenSuccess() {
            // given
            Member member = signup();
            List<Member> partners = getPartners();
            Item item = supportRepository.save(FixtureFactory.createItem("item", "가전/잡화", member));
            supportRepository.save(FixtureFactory.createChatRooms(member, partners, item));

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(null, 10, 1L);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(21),
                    () -> assertThat(response.getContents().get(0).getChatPartnerName()).isEqualTo("30testId"),
                    () -> assertThat(response.getContents().get(0).getLastSendMessage()).isEqualTo("30번 채팅방"),
                    () -> assertThat(response.getContents().get(9).getChatPartnerName()).isEqualTo("21testId")
            );
        }

        @DisplayName("마지막 페이지에서 최근 전송된 채팅 순으로 보여진다.")
        @Test
        void givenChatsDate_whenLastPage_thenSuccess() {
            // given
            Member member = signup();
            List<Member> partners = getPartners();
            Item item = supportRepository.save(FixtureFactory.createItem("item", "가전/잡화", member));
            supportRepository.save(FixtureFactory.createChatRooms(member, partners, item));

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(11L, 10, 1L);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull(),
                    () -> assertThat(response.getContents().get(0).getChatPartnerName()).isEqualTo("10testId"),
                    () -> assertThat(response.getContents().get(9).getChatPartnerName()).isEqualTo("1testId")
            );
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
}
