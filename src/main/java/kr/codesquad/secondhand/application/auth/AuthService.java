package kr.codesquad.secondhand.application.auth;

import javax.servlet.http.HttpServletRequest;
import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.DuplicatedException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtExtractor;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.member.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.member.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.member.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.member.UserResponse;
import kr.codesquad.secondhand.presentation.dto.token.AuthToken;
import kr.codesquad.secondhand.repository.RedisRepository;
import kr.codesquad.secondhand.repository.member.MemberRepository;
import kr.codesquad.secondhand.repository.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthService {

    private final ImageService imageService;
    private final ResidenceService residenceService;
    private final TokenRepository tokenRepository;
    private final MemberRepository memberRepository;
    private final NaverRequester naverRequester;
    private final JwtProvider jwtProvider;
    private final RedisRepository redisRepository;

    @Transactional
    public LoginResponse login(LoginRequest request, String code) {
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        Long memberId = verifyUser(request, userProfile);

        String refreshToken = jwtProvider.createRefreshToken(memberId);
        tokenRepository.deleteByMemberId(memberId);

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
    public void signUp(SignUpRequest request, String code, MultipartFile profile) {
        verifyDuplicated(request);
        OauthTokenResponse tokenResponse = naverRequester.getToken(code);
        UserProfile userProfile = naverRequester.getUserProfile(tokenResponse);
        if (profile != null) {
            String profileUrl = imageService.uploadImage(profile);
            userProfile.changeProfileUrl(profileUrl);
        }
        Member savedMember = saveMember(request, userProfile);
        residenceService.saveResidence(request.getAddressNames(), savedMember);
    }

    private void verifyDuplicated(SignUpRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, Long memberId) {
        String accessToken = JwtExtractor.extract(request)
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_TOKEN));
        Long expiration = jwtProvider.getExpiration(accessToken);
        redisRepository.set(accessToken, "logout", expiration);
        tokenRepository.deleteByMemberId(memberId);
    }

    private Long verifyUser(LoginRequest request, UserProfile userProfile) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA));
        if (!member.isSameEmail(userProfile.getEmail())) {
            throw new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA);
        }
        return member.getId();
    }

    private Member saveMember(SignUpRequest request, UserProfile userProfile) {
        return memberRepository.save(request.toMemberEntity(userProfile));
    }
}
