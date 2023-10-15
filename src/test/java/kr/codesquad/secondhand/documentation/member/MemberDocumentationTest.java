package kr.codesquad.secondhand.documentation.member;

import kr.codesquad.secondhand.application.member.MemberService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.documentation.DocumentationTestSupport;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import kr.codesquad.secondhand.presentation.dto.member.ModifyProfileResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MemberDocumentationTest extends DocumentationTestSupport {

    @Autowired
    private MemberService memberService;
    @Autowired
    private ResidenceService residenceService;

    @DisplayName("회원 프로필 이미지 수정")
    @Test
    void modifyProfile() throws Exception {
        // given
        MockMultipartFile profile = new MockMultipartFile("updateImageFile", "image.png", "image/png",
                "<<png data>>".getBytes());

        given(memberService.modifyProfileImage(any(MultipartFile.class), anyLong()))
                .willReturn(new ModifyProfileResponse("profile-image"));

        // when
        var builder = RestDocumentationRequestBuilders.multipart("/api/members/{loginId}", "bruni");
        builder.with(mockRequest -> {
            mockRequest.setMethod(HttpMethod.PUT.name());
            return mockRequest;
        });

        var response = mockMvc.perform(builder
                .file(profile)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        // then
        var resultActions = response.andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.profileImageUrl").value("profile-image"));

        // docs
        resultActions.andDo(document("member/modify-profile",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                requestParts(
                        partWithName("updateImageFile").description("수정할 프로필 이미지 파일")
                ),
                pathParameters(
                        parameterWithName("loginId").description("로그인 아이디 ex. bruni")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.profileImageUrl").type(STRING).description("수정된 프로필 이미지 URL")
                )
        ));
    }

    @DisplayName("회원의 주소 데이터 리스트 조회")
    @Test
    void readResidences() throws Exception {
        // given
        given(residenceService.readResidenceOfMember(anyLong()))
                .willReturn(List.of(
                        new AddressData(1L, "경기도 부천시 범박동", "범박동", true),
                        new AddressData(2L, "경기도 부천시 상동", "상동", false)));

        // when
        var response = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/members/regions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtProvider.createAccessToken(1L)));

        // then
        var resultActions = response.andExpect(status().isOk())
                .andExpect(jsonPath("statusCode").value(200))
                .andExpect(jsonPath("message").value("성공했습니다."))
                .andExpect(jsonPath("data.addresses").isArray())
                .andExpect(jsonPath("data.addresses[0].addressId").value(1L))
                .andExpect(jsonPath("data.addresses[0].fullAddressName").value("경기도 부천시 범박동"))
                .andExpect(jsonPath("data.addresses[0].addressName").value("범박동"))
                .andExpect(jsonPath("data.addresses[0].isSelected").value(true));

        // docs
        resultActions.andDo(document("member/read-residences",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("액세스 토큰을 담는 인증 헤더")
                ),
                responseFields(
                        fieldWithPath("statusCode").type(NUMBER).description("응답 상태 코드"),
                        fieldWithPath("message").type(STRING).description("응답 메시지"),
                        fieldWithPath("data.addresses").type(ARRAY).description("주소 데이터 리스트"),
                        fieldWithPath("data.addresses[].addressId").type(NUMBER).description("주소 데이터의 식별자"),
                        fieldWithPath("data.addresses[].fullAddressName").type(STRING).description("주소 데이터의 전체 주소 이름"),
                        fieldWithPath("data.addresses[].addressName").type(STRING).description("주소 데이터의 읍/면/동 주소 이름"),
                        fieldWithPath("data.addresses[].isSelected").type(BOOLEAN).description("주소 데이터의 선택 여부")
                )
        ));
    }
}
