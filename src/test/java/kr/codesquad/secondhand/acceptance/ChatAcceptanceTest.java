package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.util.Map;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.chat.WhoIsLast;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ChatAcceptanceTest extends AcceptanceTestSupport {

    private ChatRoom prepareToChatAndReturnChatRoom(Member sender) {
        Member receiver = supportRepository.save(Member.builder()
                .email("joy@naver.com")
                .loginId("joy")
                .profileUrl("profile")
                .build());

        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전", receiver));
        return supportRepository.save(ChatRoom.builder()
                .item(item)
                .subject("")
                .status(WhoIsLast.FROM)
                .receiver(receiver)
                .sender(sender)
                .build());
    }

    @Nested
    class SendMessage {

        @DisplayName("메시지가 주어지면 메시지를 전송할 때 전송하는데 성공한다.")
        @Test
        void givenChatMessage_whenSendMessage_thenSuccess() {
            // given
            Member sender = signup();
            ChatRoom chatRoom = prepareToChatAndReturnChatRoom(sender);

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(sender.getId()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("message", "안녕하세요~ 상품에 대해 물어볼게 있습니다!"));

            // when
            var response = request
                    .when()
                    .post("/api/chats/" + chatRoom.getId())
                    .then().log().all()
                    .extract();

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }
}
