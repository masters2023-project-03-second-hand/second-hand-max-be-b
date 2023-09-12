package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import java.io.File;
import java.io.IOException;
import kr.codesquad.secondhand.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class MemberAcceptanceTest extends AcceptanceTestSupport {

    private File createFakeFile() throws IOException {
        return File.createTempFile("test-image", ".png");
    }

    @DisplayName("프로필 이미지를 변경할 때 변경할 프로필 사진이 주어지면 변경에 성공한다.")
    @Test
    void givenProfileImage_whenModifyProfileImage_thenSuccess() throws IOException {
        // given
        Member member = signup();

        var request = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("updateImageFile", createFakeFile(), MediaType.IMAGE_PNG_VALUE);

        // when
        var response = request
                .when()
                .put("/api/members/" + member.getLoginId())
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
