package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.member.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class MemberAcceptanceTest extends AcceptanceTestSupport {

    private MultiPartSpecification createFakeFile() {
        return new MultiPartSpecBuilder("File content".getBytes())
                .fileName("file.png")
                .controlName("updateImageFile")
                .mimeType(MediaType.IMAGE_PNG_VALUE)
                .build();
    }

    @DisplayName("프로필 이미지를 변경할 때 변경할 프로필 사진이 주어지면 변경에 성공한다.")
    @Test
    void givenProfileImage_whenModifyProfileImage_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("updatedProfileImage");

        Member member = signup();

        var request = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart(createFakeFile());

        // when
        var response = request
                .when()
                .put("/api/members/" + member.getLoginId())
                .then().log().all()
                .extract();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(200),
                () -> assertThat(response.jsonPath().getString("data.profileImageUrl")).isEqualTo("updatedProfileImage")
        );
    }
}
