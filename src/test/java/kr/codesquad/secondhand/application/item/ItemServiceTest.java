package kr.codesquad.secondhand.application.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.SupportRepository;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.application.image.S3Uploader;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ApplicationTest
class ItemServiceTest {

    @MockBean
    private S3Uploader s3Uploader;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private ItemService itemService;

    @DisplayName("상품을 등록하면 상품 정보와 상품 이미지가 DB에 성공적으로 저장된다.")
    @Test
    void given_whenRegisterItem_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        supportRepository.save(Member.builder()
                .email("23Yong@secondhand.com")
                .loginId("bruni")
                .profileUrl("profile-url")
                .build());

        // when
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), 1L);

        // then
        Optional<Item> item = supportRepository.findById(Item.class, 1L);
        List<ItemImage> images = supportRepository.findAll(ItemImage.class);

        assertAll(
                () -> assertThat(item).isPresent(),
                () -> assertThat(images).hasSize(3)
        );
    }

    private List<MultipartFile> createFakeImage() {
        List<MultipartFile> mockMultipartFiles = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mockMultipartFiles.add(new MockMultipartFile(
                    "test-image",
                    "test-image.png",
                    MediaType.IMAGE_PNG_VALUE,
                    "image-content".getBytes(StandardCharsets.UTF_8)));
        }
        return mockMultipartFiles;
    }
}
