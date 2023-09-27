package kr.codesquad.secondhand.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
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
import kr.codesquad.secondhand.domain.image.ImageFile;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

@DisplayName("인수 테스트 - 상품")
public class ItemAcceptanceTest extends AcceptanceTestSupport {

    private File createFakeFile() throws IOException {
        return File.createTempFile("test-image", ".png");
    }

    private void saveItems(Member member) {
        for (int i = 1; i <= 20; i++) {
            supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
        }
    }

    private void saveDefaultRegionItems(Member member) {
        for (int i = 1; i <= 20; i++) {
            supportRepository.save(FixtureFactory.createDefaultRegionItem("선풍기 - " + i, "가전", member));
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

            File thumbnail = new ClassPathResource("item.jpeg").getFile();

            var request = RestAssured
                    .given().log().all()
                    .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                    .multiPart("thumbnailImage",
                            thumbnail,
                            MediaType.IMAGE_JPEG_VALUE)
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
            given(s3Uploader.uploadImageFile(any(ImageFile.class))).willReturn("thumbnail-url");
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

        @DisplayName("로그인하지 않은 사용자가 상품 목록 조회에 성공한다.")
        @Test
        void givenNotLoginMember_whenReadAllItems_thenSuccess() {
            // given
            saveDefaultRegionItems(signup());

            var request = RestAssured
                    .given().log().all()
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

        @DisplayName("로그인하지 않은 사용자가 역삼1동 외의 지역을 검색 시 401응답을 한다.")
        @Test
        void givenNotLoginMember_whenReadAllItems_thenResponse401() {
            // given
            saveDefaultRegionItems(signup());

            var request = RestAssured
                    .given().log().all()
                    .queryParam("size", 5)
                    .queryParam("region", "범안동");

            // when & then
            var response = request
                    .when()
                    .get("/api/items")
                    .then().log().all()
                    .extract();

            assertThat(response.statusCode()).isEqualTo(401);
        }
    }

    @DisplayName("판매내역을 조회할 때")
    @Nested
    class ReadAllSalesHistory {

        @DisplayName("판매 상품의 판매 상태가 주어지면 상태에 맞게 최근 등록한 판매 상품부터 상품 조회에 성공한다.")
        @Test
        void givenStatusOfItem_whenReadAll_thenSuccess() {
            // given
            Member member = signup();
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(FixtureFactory.createItem("선풍기 - " + i, "가전", member));
            }

            var request = RestAssured
                    .given().log().all()
                    .queryParam("status", "ON_SALE")
                    .header(org.springframework.http.HttpHeaders.AUTHORIZATION,
                            "Bearer " + jwtProvider.createAccessToken(member.getId()));

            // when
            var response = request
                    .when()
                    .get("/api/sales/history")
                    .then().log().all()
                    .body("data.contents.status", everyItem(allOf(is("판매중"))))
                    .extract();

            // then
            assertAll(
                    () -> assertThat(response.statusCode()).isEqualTo(200),
                    () -> assertThat(response.jsonPath().getString("data.contents[0].itemId")).isEqualTo("20"),
                    () -> assertThat(response.jsonPath().getString("data.contents[0].title")).isEqualTo("선풍기 - 20")
            );
        }
    }
}
