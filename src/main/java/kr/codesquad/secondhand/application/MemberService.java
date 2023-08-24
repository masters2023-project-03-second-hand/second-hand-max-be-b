package kr.codesquad.secondhand.application;

import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import kr.codesquad.secondhand.presentation.dto.UserResponse;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final NaverRequester naverRequester;

    public LoginResponse login(LoginRequest request, String code) {
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        verifyUser(request, userProfile);

        // todo: 애플리케이션의 Jwt토큰 만들어서 LoginResponse에 추가

        return new LoginResponse(new UserResponse(userProfile.getEmail(), userProfile.getProfileUrl()));
    }

    public void signUp(SignUpRequest request, String code) {
        verifyDuplicated(request);
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        saveMember(request, userProfile);
        // todo: 주소 저장 로직 필요
    }

    private void verifyUser(LoginRequest request, UserProfile userProfile) {
        Member member = memberRepository.findByLoginId(request.getLoginId()).orElseThrow(); // todo: 예외 던지기(존재하지 않는 회원)
        if (!member.getEmail().equals(userProfile.getEmail())) {
            throw new IllegalArgumentException(); // todo: 예외 던지기(db email과 네이버 email 불일치)
        }
    }

    private void verifyDuplicated(SignUpRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
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
}
