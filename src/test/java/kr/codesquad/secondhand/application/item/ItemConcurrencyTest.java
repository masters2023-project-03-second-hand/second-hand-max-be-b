package kr.codesquad.secondhand.application.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ItemConcurrencyTest extends ApplicationTestSupport {

    @Autowired
    private ItemReadFacade itemReadFacade;

    @DisplayName("구매자가 상품의 상세화면을 조회하면 해당 상품의 조회수가 증가한다.")
    @Test
    void given_whenBuyerReadItemDetails_thenIncreaseViewCount() throws InterruptedException {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member seller = supportRepository.save(FixtureFactory.createMember());
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", seller));
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());

        int threadCount = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(100);

        // when
        for (int i = 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    itemReadFacade.read(buyer.getId(), item.getId());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        // then
        Thread.sleep(5000L);
        Item foundItem = supportRepository.findById(Item.class, item.getId()).get();

        assertThat(foundItem.getViewCount()).isEqualTo(100);
    }

    @DisplayName("상품의 상세 페이지를 조회할 때 스케줄러에 의해 조회수가 증가한다.")
    @Test
    void given_whenItemRead_thenIncreaseViewCountByScheduler() throws InterruptedException {
        // given
        given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2", "url3"));
        Member seller = supportRepository.save(FixtureFactory.createMember());
        Item item = supportRepository.save(FixtureFactory.createItem("선풍기", "가전잡화", seller));
        Member buyer = supportRepository.save(Member.builder()
                .email("joy@secondhand.com")
                .loginId("joy")
                .profileUrl("profile-url")
                .build());

        // when
        for (int i = 0; i < 10; i++) {
            itemReadFacade.read(buyer.getId(), item.getId());
        }
        Thread.sleep(5000L);

        // then
        Item foundItem = supportRepository.findById(Item.class, item.getId()).get();

        assertThat(foundItem.getViewCount()).isEqualTo(10);

    }
}
