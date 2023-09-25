package kr.codesquad.secondhand.application.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("비즈니스 로직 - 회원")
class MemberServiceTest extends ApplicationTestSupport {

    @Autowired
    private MemberService memberService;

    @Value("${custom.default-profile}")
    private String defaultProfileUrl;

    @DisplayName("프로필 이미지를 변경할 때")
    @Nested
    class ModifyProfile {

        @DisplayName("변경할 프로필 사진이 주어지면 프로필 변경에 성공한다.")
        @Test
        void givenUpdatedProfileImage_whenModifyProfileImage_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("updatedProfileUrl");

            MockMultipartFile profileImage = new MockMultipartFile("profile-image",
                    "profile-image.jpeg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "profile-image-content".getBytes(StandardCharsets.UTF_8));

            // when
            var updatedProfileResponse = memberService.modifyProfileImage(profileImage, member.getId());

            // then
            assertThat(updatedProfileResponse.getProfileImageUrl()).isEqualTo("updatedProfileUrl");
        }

        @DisplayName("변경할 프로필 사진이 주어지지 않을 때 기본 이미지로 프로필 변경에 성공한다.")
        @Test
        void given_whenModifyProfileImage_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());

            // when
            var updatedProfileResponse = memberService.modifyProfileImage(null, member.getId());

            // then
            assertThat(updatedProfileResponse.getProfileImageUrl()).isEqualTo(defaultProfileUrl);
        }
    }
}
