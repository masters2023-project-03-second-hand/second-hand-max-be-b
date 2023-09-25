package kr.codesquad.secondhand.application.image;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import kr.codesquad.secondhand.domain.image.ImageFile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("단위 테스트 - 이미지")
@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private ImageService imageService;

    @DisplayName("이미지 파일이 주어지면 이미지 업로드에 성공한다.")
    @Test
    void givenMultipartFile_thenSuccess() {
        // given
        var mockMultipartFile = createMockMultipartFile("test.png", MediaType.IMAGE_PNG_VALUE);

        given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("url");

        // when & then
        assertThatCode(() -> imageService.uploadImage(mockMultipartFile)).doesNotThrowAnyException();
    }

    @DisplayName("여러 개의 이미지 파일이 주어지면 이미지 업로드에 성공한다.")
    @Test
    void givenMultipleMultipartFile_thenSuccess() {
        // given
        List<MultipartFile> images = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            images.add(createMockMultipartFile("test.png", MediaType.IMAGE_PNG_VALUE));
        }

        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("1", "2", "3", "4", "5"));

        // when & then
        assertThatCode(() -> imageService.uploadImages(images)).doesNotThrowAnyException();
    }

    private MockMultipartFile createMockMultipartFile(String fileName, String extension) {
        return new MockMultipartFile(
                "test-image",
                fileName,
                extension,
                "imageBytes".getBytes(StandardCharsets.UTF_8)
        );
    }
}
