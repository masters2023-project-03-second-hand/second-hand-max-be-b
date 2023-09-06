package kr.codesquad.secondhand.application.wishitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class WishItemServiceTest extends ApplicationTestSupport {

    @Autowired
    private WishItemService wishItemService;

    @DisplayName("아이템을 관심 상품으로 등록하는데 성공한다.")
    @Test
    void given_whenRegisterWishItem_thenSuccess() {
        // given
        Member member = supportRepository.save(FixtureFactory.createMember());
        supportRepository.save(FixtureFactory.createItem("초코 브라우니", "식품", member));

        // when
        wishItemService.registerWishItem(1L, 1L);

        // then
        Optional<WishItem> wishItem = supportRepository.findById(WishItem.class, 1L);

        assertAll(
                () -> assertThat(wishItem).isPresent(),
                () -> assertThat(wishItem.get().getMember().getId()).isEqualTo(1L),
                () -> assertThat(wishItem.get().getItem().getId()).isEqualTo(1L)
        );
    }

    @DisplayName("아이템을 관심 상품에서 제거하는데 성공한다.")
    @Test
    void given_whenRemoveWishItem_thenSuccess() {
        // given
        Member member = supportRepository.save(FixtureFactory.createMember());
        Item item = supportRepository.save(FixtureFactory.createItem("초코 브라우니", "식품", member));
        supportRepository.save(WishItem.builder().item(item).member(member).build());

        // when
        wishItemService.removeWishItem(1L, 1L);

        // then
        Optional<WishItem> wishItem = supportRepository.findById(WishItem.class, 1L);

        assertThat(wishItem).isNotPresent();
    }

    private Member signup() {
        return supportRepository.save(FixtureFactory.createMember());
    }

    @DisplayName("관심 상품 목록을 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("최근 관심상품으로 등록한 순서대로 상품이 조회된다.")
        @Test
        void given_whenReadAllWishItemsOfFirstPage_thenSuccess() {
            // given
            Member member = signup();
            supportRepository.save(Category.builder()
                    .name("가전")
                    .imageUrl("url")
                    .build());
            for (int i = 1; i <= 20; i++) {
                Item item = supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
                supportRepository.save(WishItem.builder()
                        .item(item)
                        .member(member)
                        .build());
            }

            // when
            CustomSlice<ItemResponse> response = wishItemService.readAll(1L, null, null, 10);

            // then
            assertAll(
                    () -> assertThat(response.getContents()).hasSize(10),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(11L),
                    () -> assertThat(response.getPaging().isHasNext()).isTrue()
            );
        }

        @DisplayName("최근 관심상품으로 등록한 순서대로 상품이 조회된다.")
        @Test
        void given_whenReadAllWishItemsOfLastPage_thenSuccess() {
            // given
            Member member = signup();
            supportRepository.save(Category.builder()
                    .name("가전")
                    .imageUrl("url")
                    .build());
            for (int i = 1; i <= 20; i++) {
                Item item = supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
                supportRepository.save(WishItem.builder()
                        .item(item)
                        .member(member)
                        .build());
            }

            // when
            CustomSlice<ItemResponse> response = wishItemService.readAll(1L, null, 11L, 10);

            // then
            assertAll(
                    () -> assertThat(response.getContents()).hasSize(10),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull(),
                    () -> assertThat(response.getPaging().isHasNext()).isFalse()
            );
        }
    }
}
