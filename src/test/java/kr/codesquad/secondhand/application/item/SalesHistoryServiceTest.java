package kr.codesquad.secondhand.application.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.item.ItemStatus;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.item.ItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class SalesHistoryServiceTest extends ApplicationTestSupport {

    @Autowired
    private SalesHistoryService salesHistoryService;

    @DisplayName("상품의 판매 상태가 주어지면 판매 목록을 조회할 때 성공한다.")
    @Test
    void givenStatusOfItem_whenReadAllSalesHistory_thenSuccess() {
        // given
        Member member = supportRepository.save(FixtureFactory.createMember());
        for (int i = 1; i <= 10; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member, ItemStatus.ON_SALE));
        }
        for (int i = 1; i <= 10; i++) {
            supportRepository.save(FixtureFactory.createItem("노트북 - " + i, "IT", member, ItemStatus.SOLD_OUT));
        }

        // when
        CustomSlice<ItemResponse> response = salesHistoryService.read(null, "SOLD_OUT", 5, member.getId());

        // then
        assertAll(
                () -> assertThat(response.getContents()).hasSize(5),
                () -> assertThat(response.getContents().get(0).getStatus()).isEqualTo(ItemStatus.SOLD_OUT),
                () -> assertThat(response.getContents().get(0).getTitle()).isEqualTo("노트북 - 10"),
                () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(16L),
                () -> assertThat(response.getPaging().isHasNext()).isTrue()
        );
    }
}
