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
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
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
        signup();

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

    @DisplayName("상품 목록을 조회할 때 첫 페이지에서 최근 등록된 상품 순으로 보여진다.")
    @Test
    void givenSavedItemData_whenReadAllItemsOfFirstPage_thenSuccess() {
        // given
        for (int i = 1; i <= 30; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", signup()));
        }

        // when
        CustomSlice<ItemResponse> response = itemService.readAll(null, null, 10);

        // then
        assertAll(
                () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(21),
                () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("선풍기 - 30"),
                () -> assertThat(response.getContents().get(9).getTitle()).isEqualTo("선풍기 - 21")
        );
    }

    @DisplayName("상품 목록을 조회할 때 두 번째 페이지에서 최근 등록된 상품 순으로 보여진다.")
    @Test
    void givenSavedItemData_whenReadAllItemsOfSecondPage_thenSuccess() {
        // given
        for (int i = 1; i <= 20; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", signup()));
        }

        // when
        CustomSlice<ItemResponse> response = itemService.readAll(11L, null, 10);

        // then
        assertAll(
                () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(1),
                () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("선풍기 - 10"),
                () -> assertThat(response.getContents().get(9).getTitle()).isEqualTo("선풍기 - 1")
        );
    }

    @DisplayName("카테고리 별 아이템 목록을 조회할 때 첫 페이지에서 해당 카테고리의 최근 등록된 상품 순으로 보여진다.")
    @Test
    void givenSavedItemDataAndCategoryId_whenReadAllItemsOfFirstPage_thenSuccess() {
        // given
        Member member = signup();
        supportRepository.save(Category.builder().name("가전").imageUrl("url").build());
        supportRepository.save(Category.builder().name("식품").imageUrl("url").build());

        for (int i = 1; i <= 10; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
        }
        for (int i = 1; i <= 5; i++) {
            supportRepository.save(FixtureFactory.createItem("맛있는 거 - " + i, "식품", member));
        }
        for (int i = 1; i <= 5; i++) {
            supportRepository.save(FixtureFactory.createItem("맛없는 거 - " + i, "식품", member));
        }

        // when
        CustomSlice<ItemResponse> response = itemService.readAll(null, 2L, 8);

        // then
        assertAll(
                () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(13),
                () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("맛없는 거 - 5"),
                () -> assertThat(response.getContents().get(7).getTitle()).isEqualTo("맛있는 거 - 3")
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

    private Member signup() {
        return supportRepository.save(FixtureFactory.createMember());
    }
}
