package kr.codesquad.secondhand.application.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.ForbiddenException;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemDetailResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.dto.item.ItemStatusRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayName("비즈니스 로직 - 아이템")
class ItemServiceTest extends ApplicationTestSupport {

    @Autowired
    private ItemService itemService;

    @DisplayName("상품을 등록하면 상품 정보와 상품 이미지가 DB에 성공적으로 저장된다.")
    @Test
    void given_whenRegisterItem_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("thumbnailUrl");
        signup();

        // when
        itemService.register(createThumbnailImage(), createFakeImage(), FixtureFactory.createItemRegisterRequest(), 1L);

        // then
        List<Item> items = supportRepository.findAll(Item.class);
        List<ItemImage> images = supportRepository.findAll(ItemImage.class);

        assertAll(
                () -> assertThat(items).isNotEmpty(),
                () -> assertThat(images).hasSize(4)
        );
    }

    @DisplayName("판매자가 상품의 상세화면을 조회한다.")
    @Test
    void given_whenSeller_thenItemDetails() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));

        // when
        ItemDetailResponse response = itemService.read(member.getId(), item.getId());

        // then
        assertAll(
                () -> assertThat(response.getIsSeller()).isTrue(),
                () -> assertThat(response.getStatus()).isEqualTo(ItemStatus.ON_SALE.getStatus()),
                () -> assertThat(response.getViewCount()).isEqualTo(0)
        );
    }

    @DisplayName("구매자가 상품의 상세화면을 조회한다.")
    @Test
    void given_whenBuyer_thenItemDetails() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member seller = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", seller));
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());
        supportRepository.save(WishItem.builder()
                .item(item)
                .member(buyer)
                .build());

        // when
        ItemDetailResponse response = itemService.read(buyer.getId(), item.getId());

        // then
        assertThat(response.getIsSeller()).isFalse();
    }

    @DisplayName("상품의 상태 수정에 성공한다.")
    @Test
    void given_whenUpdateStatus_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));
        ItemStatusRequest request = new ItemStatusRequest("예약중");

        // when
        itemService.updateStatus(request, item.getId(), member.getId());

        // then
        Item foundItem = supportRepository.findById(Item.class, 1L).get();

        assertThat(foundItem.getStatus().getStatus()).isEqualTo("예약중");
    }

    @DisplayName("작성자가 아닌 사람이 상품을 수정하려하면 예외를 던진다.")
    @Test
    void givenBuyer_whenUpdateItem_thenThrowsException() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));
        ItemStatusRequest request = new ItemStatusRequest("예약중");
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());

        // when & then
        assertThatThrownBy(() -> itemService.updateStatus(request, item.getId(), buyer.getId()))
                .isInstanceOf(ForbiddenException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.UNAUTHORIZED);
    }

    private MultipartFile createThumbnailImage() {
        return new MockMultipartFile("test-image",
                "test-image.png",
                MediaType.IMAGE_PNG_VALUE,
                "content".getBytes(StandardCharsets.UTF_8));
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

    @DisplayName("아이템을 삭제 요청 시 아이템과 이미지를 삭제한다.")
    @Test
    void given_whenDeleteItem_thenSuccess() throws InterruptedException {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));

        // when
        itemService.delete(item.getId(), member.getId());
        Thread.sleep(1000); // 비동기 로직을 위해 지연

        // then
        Optional<Item> foundItem = supportRepository.findById(Item.class, 1L);
        List<ItemImage> images = supportRepository.findAll(ItemImage.class);

        assertAll(
                () -> assertThat(foundItem).isNotPresent(),
                () -> assertThat(images).isEmpty()
        );
    }

    @DisplayName("상품을 수정할 때")
    @Nested
    class Update {

        @DisplayName("상품을 수정하면 상품정보가 db에 업데이트되고 삭제 이미지가 db에서 제거된다.")
        @Test
        void given_whenUpdateItem_thenSuccess() {
            // given
            Member member = signup();
            Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));
            for (int i = 1; i <= 4; i++) {
                supportRepository.save(ItemImage.builder()
                        .item(item)
                        .imageUrl("url" + i)
                        .build());
            }

            // when
            itemService.update(null, null, FixtureFactory.createItemUpdateRequest(), item.getId(), member.getId());

            // then
            Optional<Item> resultItem = supportRepository.findById(Item.class, item.getId());
            List<ItemImage> images = supportRepository.findAll(ItemImage.class);

            assertAll(
                    () -> assertThat(resultItem).isPresent(),
                    () -> assertThat(resultItem.get().getTitle()).isEqualTo("수정제목"),
                    () -> assertThat(images).hasSize(2)
            );
        }

        @DisplayName("상품을 수정할 때 새로운 상품 이미지가 주어지면 상품 수정에 성공한다.")
        @Test
        void givenNewImage_whenUpdateItem_thenSuccess() {
            // given
            given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("new-url"));
            Member member = signup();
            Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));
            for (int i = 1; i <= 4; i++) {
                supportRepository.save(ItemImage.builder()
                        .item(item)
                        .imageUrl("url" + i)
                        .build());
            }
            MockMultipartFile image = new MockMultipartFile(
                    "new-image",
                    "new-image.png",
                    MediaType.IMAGE_PNG_VALUE,
                    "new-image-content".getBytes(StandardCharsets.UTF_8)
            );

            // when
            itemService.update(null, List.of(image),
                    FixtureFactory.createItemUpdateRequest(), item.getId(), member.getId());

            // then
            Optional<Item> resultItem = supportRepository.findById(Item.class, item.getId());
            List<ItemImage> images = supportRepository.findAll(ItemImage.class);

            assertAll(
                    () -> assertThat(resultItem).isPresent(),
                    () -> assertThat(resultItem.get().getTitle()).isEqualTo("수정제목"),
                    () -> assertThat(images).anyMatch(itemImage -> itemImage.getImageUrl().equals("new-url"))
            );
        }

        @DisplayName("상품을 수정할 때 새로운 썸네일 이미지가 주어지면 상품 수정에 성공한다.")
        @Test
        void givenNewThumbnail_whenUpdateItem_thenSuccess() {
            // given
            given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("new-thumbnail");

            Member member = signup();
            Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", member));

            // when
            itemService.update(createThumbnailImage(), null,
                    FixtureFactory.createItemUpdateRequest(), item.getId(), member.getId());

            // then
            Optional<Item> resultItem = supportRepository.findById(Item.class, item.getId());

            assertAll(
                    () -> assertThat(resultItem).isPresent(),
                    () -> assertThat(resultItem.get().getTitle()).isEqualTo("수정제목"),
                    () -> assertThat(resultItem.get().getThumbnailUrl()).isEqualTo("new-thumbnail")
            );
        }
    }

    @DisplayName("상품 전체 목록을 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("첫 페이지에서 최근 등록된 상품 순으로 보여진다.")
        @Test
        void givenSavedItemData_whenReadAllItemsOfFirstPage_thenSuccess() {
            // given
            Member member = signup();
            for (int i = 1; i <= 30; i++) {
                supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
            }

            // when
            CustomSlice<ItemResponse> response = itemService.readAll(null, null, "범박동", 10);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(21),
                    () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("선풍기 - 30"),
                    () -> assertThat(response.getContents().get(9).getTitle()).isEqualTo("선풍기 - 21")
            );
        }

        @DisplayName("두 번째 페이지에서 최근 등록된 상품 순으로 보여진다.")
        @Test
        void givenSavedItemData_whenReadAllItemsOfSecondPage_thenSuccess() {
            // given
            Member member = signup();
            List<Item> items = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                items.add(supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member)));
            }

            // when
            var response = itemService.readAll(items.get(10).getId(), null, "범박동", 10);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull(),
                    () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("선풍기 - 10"),
                    () -> assertThat(response.getContents().get(9).getTitle()).isEqualTo("선풍기 - 1")
            );
        }

        @DisplayName("카테고리 별 아이템 목록을 조회하면 첫 페이지에서 해당 카테고리의 최근 등록된 상품 순으로 보여진다.")
        @Test
        void givenSavedItemDataAndCategoryId_whenReadAllItemsOfFirstPage_thenSuccess() {
            // given
            Member member = signup();
            supportRepository.save(Category.builder().name("가전").imageUrl("url").build());
            Category foodCategory = supportRepository.save(Category.builder().name("식품").imageUrl("url").build());

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
            var response = itemService.readAll(null, foodCategory.getId(), "범박동", 8);

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(13),
                    () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("맛없는 거 - 5"),
                    () -> assertThat(response.getContents().get(7).getTitle()).isEqualTo("맛있는 거 - 3")
            );
        }
    }
}
