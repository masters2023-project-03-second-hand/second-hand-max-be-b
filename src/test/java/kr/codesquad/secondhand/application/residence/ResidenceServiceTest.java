package kr.codesquad.secondhand.application.residence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.residence.Region;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ResidenceServiceTest extends ApplicationTestSupport {

    @Autowired
    private ResidenceService residenceService;

    @DisplayName("지역 목록을 조회할 때")
    @Nested
    class ReadAll {

        @DisplayName("지역목록의 첫 번쨰 페이지를 조회하면 주소 아이디순으로 정렬되어 조회에 성공한다.")
        @Test
        void given_whenReadAllRegionsOfFirstPage_thenSuccess() {
            // given
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(Region.builder()
                        .fullAddressName("서울특별시 강남구 신사" + i + "동")
                        .addressName("신사" + i + "동")
                        .build());
            }

            // when
            CustomSlice<RegionResponse> response = residenceService.readAllRegion(null, 10, "신사");

            // then
            assertAll(
                    () -> assertThat(response.getContents()).hasSize(10),
                    () -> assertThat(response.getContents().get(0).getAddressName()).isEqualTo("신사1동"),
                    () -> assertThat(response.getContents().get(9).getAddressName()).isEqualTo("신사10동"),
                    () -> assertThat(response.getPaging().isHasNext()).isTrue(),
                    () -> assertThat(response.getPaging().getNextCursor()).isEqualTo(10L)
            );
        }


        @DisplayName("지역목록의 마지막 페이지를 조회하면 주소 아이디순으로 정렬되어 조회에 성공한다.")
        @Test
        void given_whenReadAllRegionsOfLastPage_thenSuccess() {
            // given
            for (int i = 1; i <= 20; i++) {
                supportRepository.save(Region.builder()
                        .fullAddressName("서울특별시 강남구 신사" + i + "동")
                        .addressName("신사" + i + "동")
                        .build());
            }

            // when
            CustomSlice<RegionResponse> response = residenceService.readAllRegion(10L, 10, "신사");

            // then
            assertAll(
                    () -> assertThat(response.getContents()).hasSize(10),
                    () -> assertThat(response.getContents().get(0).getAddressName()).isEqualTo("신사11동"),
                    () -> assertThat(response.getContents().get(9).getAddressName()).isEqualTo("신사20동"),
                    () -> assertThat(response.getPaging().isHasNext()).isFalse(),
                    () -> assertThat(response.getPaging().getNextCursor()).isNull()
            );
        }
    }
}
