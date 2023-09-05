package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class WishItemAcceptanceTest extends AcceptanceTestSupport {

    @DisplayName("관심 상품 등록/해제 쿼리파라미터가 주어지면 어떤 상품을 관심상품으로 등록할 때 성공한다.")
    @Test
    void givenIsWishItemQueryParam_whenRegisterWishItem_thenSuccess() {
        // given
        Member member = signup();
        supportRepository.save(FixtureFactory.createItem("초코 브라우니", "식품", member));

        var request = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .queryParam("wish", "yes");

        // when
        var response = request
                .when()
                .post("/api/wishes/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }

    @DisplayName("관심 상품 등록/해제 쿼리파라미터가 주어지면 어떤 상품을 관심상품에서 해제할 때 성공한다.")
    @Test
    void givenIsWishItemQueryParam_whenRemoveWishItem_thenSuccess() {
        // given
        Member member = signup();
        Item item = supportRepository.save(FixtureFactory.createItem("초코 브라우니", "식품", member));
        supportRepository.save(WishItem.builder()
                .item(item)
                .member(member)
                .build());

        var request = RestAssured
                .given().log().all()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .queryParam("wish", "no");

        // when
        var response = request
                .when()
                .post("/api/wishes/1")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(200);
    }
}
