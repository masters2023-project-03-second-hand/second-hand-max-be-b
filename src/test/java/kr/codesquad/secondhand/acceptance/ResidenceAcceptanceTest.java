package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.residence.Region;
import kr.codesquad.secondhand.domain.residence.Residence;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class ResidenceAcceptanceTest extends AcceptanceTestSupport {

    private Region saveResidence(String fullAddress, String address) {
        return supportRepository.save(Region.builder()
                .fullAddressName(fullAddress)
                .addressName(address)
                .build());
    }

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

    @DisplayName("사용자의 거주 지역을 추가할 때")
    @Nested
    class Register {

        @DisplayName("읍면동 주소가 주어지면 등록에 성공한다.")
        @Test
        void givenAddressName_whenRegisterResidence_thenSuccess() {
            // given
            Member member = signup();
            saveResidence("경기도 부천시 범안동", "범안동");

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("fullAddress", "경기도 부천시 범안동", "address", "범안동"));

            // when
            var response = registerResidence(request);

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }

        @DisplayName("사용자가 이미 두 개의 거주지역을 가지고 있으면 400응답을 받는다.")
        @Test
        void givenAlreadyHasTwoResidenceMember_whenRegisterResidence_thenResponse400() {
            // given
            Member member = signup();
            Region beoman = saveResidence("경기도 부천시 범안동", "범안동");
            Region okgil = saveResidence("경기도 부천시 옥길동", "옥길동");

            supportRepository.save(Residence.from(member.getId(), beoman.getId(), "범안동"));
            supportRepository.save(Residence.from(member.getId(), okgil.getId(), "옥길동"));

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("fullAddress", "경기도 부천시 오류동", "address", "오류동"));

            // when
            var response = registerResidence(request);

            // then
            assertThat(response.statusCode()).isEqualTo(400);
        }

        private ExtractableResponse<Response> registerResidence(RequestSpecification request) {
            return request
                    .when()
                    .post("/api/regions")
                    .then().log().all()
                    .extract();
        }
    }

    @DisplayName("사용자의 거주 지역을 제거할 때")
    @Nested
    class Remove {

        @DisplayName("읍면동 주소가 주어지면 제거에 성공한다.")
        @Test
        void givenAddressName_whenRemoveResidence_thenSuccess() {
            // given
            Member member = signup();
            Region beoman = saveResidence("경기도 부천시 범안동", "범안동");
            Region okgil = saveResidence("경기도 부천시 옥길동", "옥길동");
            supportRepository.save(Residence.from(member.getId(), beoman.getId(), "범안동"));
            supportRepository.save(Residence.from(member.getId(), okgil.getId(), "옥길동"));
            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("fullAddress", "경기도 부천시 범안동", "address", "범안동"));

            // when
            var response = removeResidence(request);

            // then
            assertThat(response.statusCode()).isEqualTo(200);
        }

        @DisplayName("거주 지역을 한 곳만 가지고 있는 회원이 주어지면 400 응답을 한다.")
        @Test
        void givenMemberWhoHasOnlyOneResidence_whenRemoveResidence_thenResponse400() {
            // given
            Member member = signup();
            Region beoman = saveResidence("경기도 부천시 범안동", "범안동");
            supportRepository.save(Residence.from(member.getId(), beoman.getId(), "범안동"));

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(member.getId()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(Map.of("fullAddress", "경기도 부천시 범안동", "address", "범안동"));

            // when
            var response = removeResidence(request);

            // then
            assertThat(response.statusCode()).isEqualTo(400);
        }

        private ExtractableResponse<Response> removeResidence(RequestSpecification request) {
            return request
                    .when()
                    .delete("/api/regions")
                    .then().log().all()
                    .extract();
        }
    }
}
