package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.anyList;
import static org.mockito.BDDMockito.given;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.io.IOException;
import java.util.List;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ItemAcceptanceTest extends AcceptanceTestSupport {

    private File createFakeFile() throws IOException {
        return File.createTempFile("test-image", ".png");
    }

    private void saveItems(Member member) {
        for (int i = 1; i <= 20; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
        }
    }

    @DisplayName("상품 등록할 때")
    @Nested
    class Register {

        @DisplayName("상품 이미지와 상품 등록정보가 주어지면 상품 등록에 성공한다.")
        @Test
        void givenImagesAndItemData_whenRegisterItem_thenSuccess() throws Exception {
            // given
            givenSetUp();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .multiPart("images",
                            createFakeFile(),
                            MediaType.IMAGE_PNG_VALUE)
                    .multiPart("images",
                            createFakeFile(),
                            MediaType.IMAGE_PNG_VALUE)
                    .multiPart("item",
                            objectMapper.writeValueAsString(FixtureFactory.createItemRegisterRequest()),
                            MediaType.APPLICATION_JSON_VALUE);

            // when
            var response = registerItem(request);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(201),
                    () -> assertThat(response.jsonPath().getInt("statusCode")).isEqualTo(201)
            );
        }

        @DisplayName("상품 이미지가 아예 주어지지 않으면 400 응답코드로 응답한다.")
        @Test
        void givenNoImageAndItemData_whenRegisterItem_thenResponse400() throws Exception {
            // given
            givenSetUp();

            // when
            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .multiPart("item",
                            objectMapper.writeValueAsString(FixtureFactory.createItemRegisterRequest()),
                            MediaType.APPLICATION_JSON_VALUE);

            // when
            var response = registerItem(request);

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(400),
                    () -> assertThat(response.jsonPath().getInt("statusCode")).isEqualTo(400),
                    () -> assertThat(response.jsonPath().getString("message")).isNotNull()
            );
        }

        private void givenSetUp() {
            // objectMapper 한글 인코딩을 위한 설정
            objectMapper.getFactory().configure(JsonWriteFeature.ESCAPE_NON_ASCII.mappedFeature(), true);

            signup();
            given(s3Uploader.uploadImageFiles(anyList())).willReturn(List.of("url1", "url2"));
        }

        private ExtractableResponse<Response> registerItem(RequestSpecification request) {
            return request
                    .when()
                    .post("/api/items")
                    .then().log().all()
                    .extract();
        }
    }

    @DisplayName("상품 전체 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("첫 페이지 조회시 지정한 사이즈만큼 상품 목록이 최근 등록 순으로 보여진다.")
        @Test
        void givenSavedItem_whenReadAllItemsOfFirstPage_thenSuccess() {
            // given
            saveItems(signup());

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .queryParam("region", "범박동")
                    .queryParam("size", 5);

            // when & then
            var response = request
                    .when()
                    .get("/api/items")
                    .then().log().all()
                    .statusCode(200)
                    .extract().response().jsonPath();

            assertAll(
                    () -> assertThat(response.getString("data.contents[0].title")).isEqualTo("선풍기 - 20"),
                    () -> assertThat(response.getString("data.contents[4].title")).isEqualTo("선풍기 - 16"),
                    () -> assertThat(response.getInt("data.paging.nextCursor")).isEqualTo(16),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isTrue()
            );
        }

        @DisplayName("중간 페이지 조회시 지정한 사이즈 만큼 상품 목록이 최근 등록 순으로 보여진다.")
        @Test
        void givenSavedItem_whenReadAllItemsOfMiddlePage_thenSuccess() {
            // given
            saveItems(signup());

            var request = RestAssured
                    .given().log().all()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .queryParam("region", "범박동")
                    .queryParam("size", 5)
                    .queryParam("cursor", 16);

            // when & then
            var response = request
                    .when()
                    .get("/api/items")
                    .then().log().all()
                    .statusCode(200)
                    .extract().response().jsonPath();

            assertAll(
                    () -> assertThat(response.getString("data.contents[0].title")).isEqualTo("선풍기 - 15"),
                    () -> assertThat(response.getString("data.contents[4].title")).isEqualTo("선풍기 - 11"),
                    () -> assertThat(response.getInt("data.paging.nextCursor")).isEqualTo(11),
                    () -> assertThat(response.getBoolean("data.paging.hasNext")).isTrue()
            );
        }
    }
}
