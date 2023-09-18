package kr.codesquad.secondhand.application.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.application.ApplicationTest;
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
            List<ChatRoom> chatRooms = (List<ChatRoom>) supportRepository.save(FixtureFactory.createChatRooms(member, partners, item));

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(null, 10, 1L);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(21),
                    () -> assertThat(response.getContents().get(0).getChatPartnerName()).isEqualTo("30testId"),
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
            List<ChatRoom> chatRooms = FixtureFactory.createChatRooms(member, partners, item);

            // when
            CustomSlice<ChatRoomResponse> response = chatRoomService.read(21L, 10, 1L);


            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull()
            );

        }

        private Member signup() {
            return supportRepository.save(FixtureFactory.createMember());
        }

        private List<Member> getPartners() {
            return (List<Member>) supportRepository.save(FixtureFactory.createPartner());
        }
    }
}
