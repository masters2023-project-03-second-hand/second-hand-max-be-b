package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import java.util.Map;
import kr.codesquad.secondhand.domain.chat.ChatRoom;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@DisplayName("인수 테스트 - 채팅")
public class ChatAcceptanceTest extends AcceptanceTestSupport {

    private ChatRoom prepareToChatAndReturnChatRoom(Member sender) {
        Member seller = supportRepository.save(Member.builder()
                .email("joy@naver.com")
                .loginId("joy")
                .profileUrl("profile")
                .build());

        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전", seller));
        return supportRepository.save(ChatRoom.builder()
                .item(item)
                .subject("")
                .seller(seller)
                .buyer(sender)
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

    @DisplayName("채팅방이 생성될 때")
    @Nested
    class CreateChatRoom {

        @DisplayName("채팅방 생성에 성공한다.")
        @Test
        void given_whenCreateChatRoom_thenSuccess() {
            // given
            Member member = signup();
            Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전", member));

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()));

            // when
            var response = request
                    .when()
                    .post("/api/items/" + item.getId() + "/chats")
                    .then().log().all()
                    .extract();

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(201),
                    () -> assertThat(response.jsonPath().getString("data.chatRoomId"))
            );
        }
    }
}
