package kr.codesquad.secondhand.application.residence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import kr.codesquad.secondhand.application.ApplicationTestSupport;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.residence.Region;
import kr.codesquad.secondhand.domain.residence.Residence;
import kr.codesquad.secondhand.exception.BadRequestException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.fixture.FixtureFactory;
import kr.codesquad.secondhand.presentation.dto.CustomSlice;
import kr.codesquad.secondhand.presentation.dto.residence.RegionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("비즈니스 로직 - 거주지역")
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

    @DisplayName("사용자의 거주 지역을 추가할 때")
    @Nested
    class Register {

        @DisplayName("읍면동 주소가 주어지면 성공한다.")
        @Test
        void givenAddressName_whenRegisterResidence_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Region beomBak = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 범박동")
                    .addressName("범박동")
                    .build());

            // when & then
            assertThatCode(() -> residenceService.register(beomBak.getId(), member.getId())).doesNotThrowAnyException();
        }

        @DisplayName("사용자가 이미 두 개의 거주지역을 가지고 있으면 예외를 던진다.")
        @Test
        void givenAlreadyHasTwoResidenceMember_whenRegisterResidence_thenThrowsException() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Region beombak = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 범박동")
                    .addressName("범박동")
                    .build());
            Region okgil = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 옥길동")
                    .addressName("옥길동")
                    .build());
            Region guaean = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 괴안동")
                    .addressName("괴안동")
                    .build());
            supportRepository.save(Residence.of(member.getId(), beombak.getId(), "범박동")).selectToMainResidence();
            supportRepository.save(Residence.of(member.getId(), okgil.getId(), "옥길동"));

            // when & then
            assertThatThrownBy(() -> residenceService.register(guaean.getId(), member.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .extracting("errorCode").isEqualTo(ErrorCode.INVALID_REQUEST);
        }
    }

    @DisplayName("사용자의 거주 지역을 삭제할 때")
    @Nested
    class Remove {

        @DisplayName("읍면동 주소가 주어지면 삭제에 성공한다.")
        @Test
        void givenAddressName_whenRemoveResidence_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Region beombak = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 범박동")
                    .addressName("범박동")
                    .build());
            Region okgil = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 옥길동")
                    .addressName("옥길동")
                    .build());
            supportRepository.save(Residence.of(member.getId(), beombak.getId(), "범박동")).selectToMainResidence();
            supportRepository.save(Residence.of(member.getId(), okgil.getId(), "옥길동"));

            // when & then
            assertThatCode(() -> residenceService.remove(beombak.getId(), member.getId()))
                    .doesNotThrowAnyException();
        }

        @DisplayName("거주 지역을 한 곳만 가지고 있는 회원이 주어지면 예외를 던진다.")
        @Test
        void givenMemberWhoHasOnlyOneResidence_whenRemoveResidence_thenThrowsException() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Region beombak = supportRepository.save(Region.builder()
                    .fullAddressName("경기도 부천시 범박동")
                    .addressName("범박동")
                    .build());
            supportRepository.save(Residence.of(member.getId(), beombak.getId(), "범박동")).selectToMainResidence();

            // when & then
            assertThatThrownBy(() -> residenceService.remove(beombak.getId(), member.getId()))
                    .isInstanceOf(BadRequestException.class)
                    .extracting("errorCode").isEqualTo(ErrorCode.INVALID_REQUEST);
        }
    }

    @DisplayName("사용자가 메인 거주 지역을 선택할 때")
    @Nested
    class Select {

        @DisplayName("지역 아이디가 주어지면 선택에 성공한다.")
        @Test
        void givenSelectedAddressId_whenSelectResidence_thenSuccess() {
            // given
            Member member = supportRepository.save(FixtureFactory.createMember());
            Region beoman = supportRepository.save(Region.builder().addressName("범안동")
                    .fullAddressName("경기도 부천시 범안동")
                    .build());
            Region okgil = supportRepository.save(Region.builder().addressName("옥길동")
                    .fullAddressName("경기도 부천시 옥길동")
                    .build());

            Residence mainResidence = supportRepository.save(Residence.of(member.getId(), beoman.getId(), "범안동"));
            Residence residence = supportRepository.save(Residence.of(member.getId(), okgil.getId(), "범안동"));

            mainResidence.selectToMainResidence();

            // when
            residenceService.selectResidence(okgil.getId(), member.getId());

            // then
            Residence notSelectedResidence = supportRepository.findById(Residence.class, mainResidence.getId()).get();
            Residence selectedResidence = supportRepository.findById(Residence.class, residence.getId()).get();

            assertAll(
                    () -> assertThat(notSelectedResidence.isSelected()).isFalse(),
                    () -> assertThat(selectedResidence.isSelected()).isTrue()
            );
        }
    }
}
