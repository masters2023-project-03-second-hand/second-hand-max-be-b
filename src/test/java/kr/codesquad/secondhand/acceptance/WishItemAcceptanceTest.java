package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import kr.codesquad.secondhand.domain.category.Category;
import kr.codesquad.secondhand.domain.item.Item;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.wishitem.WishItem;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    private void saveItemsAndWishItemsAndCategory(Member member) {
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
    }

    @DisplayName("관심 상품 목록을 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("첫 번쨰 페이지를 조회하면 최근 등록한 관심 상품 순서로 관심 상품 목록이 조회된다.")
        @Test
        void given_whenReadAllWishItemsOfFirstPage_thenSuccess() {
            // given
            Member member = signup();
            saveItemsAndWishItemsAndCategory(member);

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L));

            // when
            var response = readAll(request);

            // then
            assertAll(
                    () -> assertThat(response.getString("data.contents[0].title")).isEqualTo("선풍기 - 20"),
                    () -> assertThat(response.getString("data.contents[9].title")).isEqualTo("선풍기 - 11"),
                    () -> assertThat(response.getLong("data.paging.nextCursor")).isEqualTo(11L),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isTrue()
            );
        }

        @DisplayName("마지막 페이지를 조회하면 최근 등록한 관심 상품 순서로 관심 상품 목록이 조회된다.")
        @Test
        void givenCategoryId_whenReadAllWishItemsOfLastPage_thenSuccess() {
            // given
            Member member = signup();
            saveItemsAndWishItemsAndCategory(member);

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .queryParam("cursor", 11L);

            // when
            var response = readAll(request);

            // then
            assertAll(
                    () -> assertThat(response.getString("data.contents[0].title")).isEqualTo("선풍기 - 10"),
                    () -> assertThat(response.getString("data.contents[9].title")).isEqualTo("선풍기 - 1"),
                    () -> assertThat(response.getObject("data.paging.nextCursor", Long.class)).isNull(),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isFalse()
            );
        }

        private JsonPath readAll(RequestSpecification request) {
            return request
                    .when()
                    .get("/api/wishes")
                    .then().log().all()
                    .statusCode(200)
                    .extract().response().jsonPath();
        }
    }
}
