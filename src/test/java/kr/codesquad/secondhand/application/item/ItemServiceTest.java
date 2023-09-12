package kr.codesquad.secondhand.application.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTest;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.domain.itemimage.ItemImage;
import kr.codesquad.secondhand.domain.member.Member;
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

@ApplicationTest
class ItemServiceTest extends ApplicationTestSupport {

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

    @DisplayName("판매자가 상품의 상세화면을 조회한다.")
    @Test
    void given_whenSeller_thenItemDetails() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), member.getId());

        // when
        ItemDetailResponse response = itemService.read(member.getId(), 1L);

        // then
        assertAll(
                () -> assertThat(response.isSeller()).isTrue(),
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
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), seller.getId());
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());

        // when
        ItemDetailResponse response = itemService.read(buyer.getId(), 1L);

        // then
        assertAll(
                () -> assertThat(response.isSeller()).isFalse(),
                () -> assertThat(response.getViewCount()).isEqualTo(1)
        );
    }

    @DisplayName("상품을 수정하면 상품정보가 db에 업데이트되고 삭제 이미지가 db에서 제거된다.")
    @Test
    void given_whenUpdateItem_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), member.getId());

        // when
        itemService.update(null, FixtureFactory.createItemUpdateRequest(), 1L, member.getId());

        // then
        Optional<Item> item = supportRepository.findById(Item.class, 1L);
        List<ItemImage> images = supportRepository.findAll(ItemImage.class);

        assertAll(
                () -> assertThat(item).isPresent(),
                () -> assertThat(item.get().getTitle()).isEqualTo("수정제목"),
                () -> assertThat(item.get().getThumbnailUrl()).isEqualTo("url3"),
                () -> assertThat(images).hasSize(1)
        );
    }

    @DisplayName("상품의 상태 수정에 성공한다.")
    @Test
    void given_whenUpdateStatus_thenSuccess() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), member.getId());
        ItemStatusRequest request = new ItemStatusRequest("예약중");

        // when
        itemService.updateStatus(request, 1L, member.getId());

        // then
        Item item = supportRepository.findById(Item.class, 1L).get();

        assertThat(item.getStatus().getStatus()).isEqualTo("예약중");
    }

    @DisplayName("작성자가 아닌 사람이 상품을 수정하려하면 예외를 던진다.")
    @Test
    void givenBuyer_whenUpdateItem_thenThrowsException() {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member member = signup();
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), member.getId());
        ItemStatusRequest request = new ItemStatusRequest("예약중");
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());

        // when & then
        assertThatThrownBy(() -> itemService.updateStatus(request, 1L, buyer.getId()))
                .isInstanceOf(ForbiddenException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.UNAUTHORIZED);
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
        signup();
        itemService.register(createFakeImage(), FixtureFactory.createItemRegisterRequest(), 1L);

        // when
        itemService.delete(1L, 1L);
        Thread.sleep(1000); // 비동기 로직을 위해 지연

        // then
        Optional<Item> item = supportRepository.findById(Item.class, 1L);
        List<ItemImage> images = supportRepository.findAll(ItemImage.class);

        assertAll(
                () -> assertThat(item).isNotPresent(),
                () -> assertThat(images).isEmpty()
        );
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
            CustomSlice<ItemResponse> response = itemService.readAll(null, null, "범박동", 10, member.getId());

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
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", signup()));
            }

            // when
            CustomSlice<ItemResponse> response = itemService.readAll(11L, null, "범박동", 10, member.getId());

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
            CustomSlice<ItemResponse> response = itemService.readAll(null, 2L, "범박동", 8, member.getId());

            // then
            assertAll(
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(13),
                    () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("맛없는 거 - 5"),
                    () -> assertThat(response.getContents().get(7).getTitle()).isEqualTo("맛있는 거 - 3")
            );
        }

        @DisplayName("로그인하지 않은 사용자가 상품목록 화면 조회에 성공한다.")
        @Test
        void givenNonLoginMember_whenReadAllItems_thenSuccess() {
            // given
            Member member = signup();
            supportRepository.save(Category.builder().name("가전").imageUrl("url").build());
            supportRepository.save(Category.builder().name("식품").imageUrl("url").build());

            for (int i = 1; i <= 10; i++) {
                supportRepository.save(FixtureFactory.createDefaultRegionItem("선풍기 - " + i, "가전", member));
            }

            // when
            CustomSlice<ItemResponse> response = itemService.readAll(null, null, "역삼1동", 10, null);

            // then
            assertThat(response.getContents().size()).isEqualTo(10);
        }

        @DisplayName("로그인하지 않은 사용자가 특정 지역의 상품목록 화면 조회시 역삼1동의 상품목록이 보여진다.")
        @Test
        void givenNonLoginMember_whenReadAllItemsByRegion_thenSuccess() {
            // given
            Member member = signup();
            supportRepository.save(Category.builder().name("가전").imageUrl("url").build());
            supportRepository.save(Category.builder().name("식품").imageUrl("url").build());

            for (int i = 1; i <= 10; i++) {
                supportRepository.save(FixtureFactory.createDefaultRegionItem("선풍기 - " + i, "가전", member));
            }

            // when
            CustomSlice<ItemResponse> response = itemService.readAll(null, null, "역삼1동", 10, -1L);

            // then
            assertThat(response.getContents()).hasSize(10);
        }
    }
}
