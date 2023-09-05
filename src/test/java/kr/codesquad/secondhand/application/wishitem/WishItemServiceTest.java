package kr.codesquad.secondhand.application.wishitem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.WishItem;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
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
}
