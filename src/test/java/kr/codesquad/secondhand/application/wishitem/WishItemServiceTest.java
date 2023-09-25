package kr.codesquad.secondhand.application.wishitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import kr.codesquad.secondhand.presentation.support.converter.IsWish;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("비즈니스 로직 - 관심상품")
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
        wishItemService.changeWishStatusOfItem(1L, 1L, IsWish.YES);

        // then
        Optional<WishItem> wishItem = supportRepository.findById(WishItem.class, 1L);
        Item item = supportRepository.findById(Item.class, 1L).get();

        assertAll(
                () -> assertThat(wishItem).isPresent(),
                () -> assertThat(wishItem.get().getMember().getId()).isEqualTo(1L),
                () -> assertThat(wishItem.get().getItem().getId()).isEqualTo(1L),
                () -> assertThat(item.getWishCount()).isEqualTo(1)
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
        wishItemService.changeWishStatusOfItem(1L, 1L, IsWish.NO);

        // then
        Optional<WishItem> wishItem = supportRepository.findById(WishItem.class, 1L);
        Item noWishItem = supportRepository.findById(Item.class, 1L).get();

        assertAll(
                () -> assertThat(wishItem).isNotPresent(),
                () -> assertThat(noWishItem.getWishCount()).isEqualTo(-1)
        );
    }

    private Member signup() {
        return supportRepository.save(FixtureFactory.createMember());
    }

    @DisplayName("관심상품 목록 화면의 카테고리 목록 조회에 성공한다.")
    @Test
    void given_whenReadWishItemCategories_thenSuccess() {
        //given
        Member member = signup();
        List<Item> list = new ArrayList<>();
        list.add(supportRepository.save(FixtureFactory.createItem("item1", "생활가전", member)));
        list.add(supportRepository.save(FixtureFactory.createItem("item2", "식물", member)));
        list.add(supportRepository.save(FixtureFactory.createItem("item4", "생활가전", member)));
        list.add(supportRepository.save(FixtureFactory.createItem("item5", "중고차", member)));
        list.add(supportRepository.save(FixtureFactory.createItem("item6", "가공식품", member)));
        supportRepository.save(FixtureFactory.createItem("NonWishItem", "유아도서", member));

        for (int i = 0; i < list.size(); i++) {
            wishItemService.changeWishStatusOfItem(i + 1L, member.getId(), IsWish.YES);
        }

        // when
        List<String> response = wishItemService.readCategories(member.getId());

        // then
        assertAll(
                () -> assertThat(response.size()).isEqualTo(4),
                () -> assertThat(response.get(0)).isEqualTo("가공식품"),
                () -> assertThat(response.contains("유아도서")).isFalse()
        );
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
