package kr.codesquad.secondhand.domain.image;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@DisplayName("도메인 - 이미지")
class ImageFileTest {

    @DisplayName("올바른 이미지 파일이 들어와 ImageFile 인스턴스 생성에 성공한다.")
    @Test
    void givenMockImageFile_whenCreateImageFile_thenSuccess() {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test-image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                "imageBytes".getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        assertThatCode(() -> ImageFile.from(mockMultipartFile)).doesNotThrowAnyException();
    }

    @DisplayName("지원하지 않는 이미지 확장자가 들어오면 예외를 던진다.")
    @Test
    void givenNotSupportedFileExtension_whenCreateImageFile_thenThrowsException() {
        // given
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "test-image",
                "image.gif",
                MediaType.IMAGE_GIF_VALUE,
                "imageBytes".getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        assertThatThrownBy(() -> ImageFile.from(mockMultipartFile))
                .isInstanceOf(BadRequestException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.INVALID_FILE_EXTENSION);
    }
}
