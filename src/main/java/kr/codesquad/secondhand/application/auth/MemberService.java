package kr.codesquad.secondhand.application.auth;

import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.DuplicatedException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.dto.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.UserProfile;
import kr.codesquad.secondhand.presentation.dto.UserResponse;
import kr.codesquad.secondhand.presentation.dto.token.AuthToken;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import kr.codesquad.secondhand.repository.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final NaverRequester naverRequester;
    private final JwtProvider jwtProvider;

    public LoginResponse login(LoginRequest request, String code) {
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        Long memberId = verifyUser(request, userProfile);

        String refreshToken = jwtProvider.createRefreshToken(memberId);
        tokenRepository.save(RefreshToken.builder()
                .memberId(memberId)
                .token(refreshToken)
                .build());
        return new LoginResponse(
                new AuthToken(jwtProvider.createAccessToken(memberId), refreshToken),
                new UserResponse(userProfile.getEmail(), userProfile.getProfileUrl())
        );
    }

    @Transactional
    public void signUp(SignUpRequest request, String code) {
        verifyDuplicated(request);
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        saveMember(request, userProfile);
        // todo: 주소 저장 로직 필요
    }

    private Long verifyUser(LoginRequest request, UserProfile userProfile) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA));
        if (!member.getEmail().equals(userProfile.getEmail())) {
            throw new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA);
        }
        return member.getId();
    }

    private void verifyDuplicated(SignUpRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_LOGIN_ID);
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
