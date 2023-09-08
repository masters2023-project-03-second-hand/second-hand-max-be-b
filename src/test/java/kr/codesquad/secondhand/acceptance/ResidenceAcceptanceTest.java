package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import kr.codesquad.secondhand.domain.residence.Region;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ResidenceAcceptanceTest extends AcceptanceTestSupport {

    @DisplayName("지역 목록을 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("검색 지역이 파라미터로 들어오면 지역 아이디가 오름차순으로 조회된다.")
        @Test
        void givenRegionParameter_whenReadAll_thenSuccess() {
            // given
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(Region.builder()
                        .fullAddressName("서울특별시 강남구 신사" + i + "동")
                        .addressName("신사" + i + "동")
                        .build());
            }
            var request = RestAssured
                    .given().log().all()
                    .queryParam("region", "신사");

            // when
            var response = request
                    .when()
                    .get("/api/regions")
                    .then().log().all()
                    .statusCode(200)
                    .extract().jsonPath();

            // then
            assertAll(
                    () -> assertThat(response.getString("data.contents[0].addressName")).isEqualTo("신사1동"),
                    () -> assertThat(response.getString("data.contents[9].addressName")).isEqualTo("신사10동"),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isTrue(),
                    () -> assertThat(response.getLong("data.paging.nextCursor")).isEqualTo(10L)
            );
        }

        @DisplayName("존재하지 않은 검색 지역이 파라미터로 들어오면 아무 것도 조회되지 않는다.")
        @Test
        void givenNotExistsRegionParameter_whenReadAll_thenSuccess() {
            // given
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(Region.builder()
                        .fullAddressName("서울특별시 강남구 신사" + i + "동")
                        .addressName("신사" + i + "동")
                        .build());
            }
            var request = RestAssured
                    .given().log().all()
                    .queryParam("region", "범박");

            // when
            var response = request
                    .when()
                    .get("/api/regions")
                    .then().log().all()
                    .statusCode(200)
                    .extract().jsonPath();

            // then
            assertAll(
                    () -> assertThat(response.getList("data.contents")).isEmpty(),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isFalse(),
                    () -> assertThat(response.getObject("data.paging.nextCursor", Long.class)).isNull()
            );
        }
    }
}
