package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import kr.codesquad.secondhand.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Map;

public class FcmAcceptanceTest extends AcceptanceTestSupport {

    @DisplayName("FCM 토큰을 저장하는데 성공한다.")
    @Test
    void saveFcmToken() {
        // given
        Member member = signup();
        String tokenValue = "testTokenValue";

        var request = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("token", tokenValue));

        // when
        var response = request
                .patch("/api/fcm-token")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
