package kr.codesquad.secondhand.application;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.infrastructure.OauthProvider;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import kr.codesquad.secondhand.presentation.dto.UserResponse;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final OauthProvider oauthProvider;
    private final MemberRepository memberRepository;

    public LoginResponse login(LoginRequest request, String code) {
        OauthTokenResponse tokenResponse = getToken(code);
        UserProfile userProfile = getUserProfile(tokenResponse);
        verifyUser(request, userProfile);

        // todo: 애플리케이션의 Jwt토큰 만들어서 LoginResponse에 추가

        return new LoginResponse(new UserResponse(userProfile.getEmail(), userProfile.getProfileUrl()));
    }

    public void signUp(SignUpRequest request, String code) {
        OauthTokenResponse tokenResponse = getToken(code);
        UserProfile userProfile = getUserProfile(tokenResponse);
        verifyDuplicated(request);
        saveMember(request, userProfile);
    }

    private void verifyUser(LoginRequest request, UserProfile userProfile) {
        Member member = memberRepository.findByLoginId(request.getLoginId()).orElseThrow(); // todo: 예외 던지기(존재하지 않는 회원)
        if (!member.getEmail().equals(userProfile.getEmail())) {
            throw new IllegalArgumentException(); // todo: 예외 던지기(db email과 네이버 email 불일치)
        }
    }

    private void verifyDuplicated(SignUpRequest request) {
        Optional<Member> existingMember = memberRepository.findByLoginId(request.getLoginId());
        if (existingMember.isPresent()) {
            throw new IllegalArgumentException(); // todo: 예외 던지기(존재하는 loginId)
        }
    }

    private Member saveMember(SignUpRequest request, UserProfile userProfile) {
        Member member = Member.builder()
                .loginId(request.getLoginId())
                .email(userProfile.getEmail())
                .profileUrl(userProfile.getProfileUrl())
                .build();
        return memberRepository.save(member);
    }

    private OauthTokenResponse getToken(String code) {
        return WebClient.create()
                .post()
                .uri(oauthProvider.getTokenUrl())
                .headers(header -> {
                    header.setBasicAuth(oauthProvider.getClientId(), oauthProvider.getClientSecret());
                    header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code))
                .retrieve()
                .bodyToMono(OauthTokenResponse.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", oauthProvider.getRedirectUrl());
        return formData;
    }

    private UserProfile getUserProfile(OauthTokenResponse tokenResponse) {
        Map<String, Object> userAttributes = getUserAttributes(tokenResponse);
        return UserProfile.builder()
                .email((String) userAttributes.get("email"))
                .profileUrl((String) userAttributes.get("profile_image"))
                .build();
    }

    private Map<String, Object> getUserAttributes(OauthTokenResponse tokenResponse) {
        return WebClient.create()
                .get()
                .uri(oauthProvider.getUserInfoUrl())
                .headers(header -> header.setBearerAuth(tokenResponse.getAccessToken()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();
    }
}
