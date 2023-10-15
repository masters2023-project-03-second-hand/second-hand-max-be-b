package kr.codesquad.secondhand.application.auth;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import kr.codesquad.secondhand.application.image.ImageService;
import kr.codesquad.secondhand.application.redis.RedisService;
import kr.codesquad.secondhand.application.residence.ResidenceService;
import kr.codesquad.secondhand.domain.member.Member;
import kr.codesquad.secondhand.domain.member.OAuthProvider;
import kr.codesquad.secondhand.domain.member.UserProfile;
import kr.codesquad.secondhand.domain.token.RefreshToken;
import kr.codesquad.secondhand.exception.DuplicatedException;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.UnAuthorizedException;
import kr.codesquad.secondhand.infrastructure.jwt.JwtExtractor;
import kr.codesquad.secondhand.infrastructure.jwt.JwtProvider;
import kr.codesquad.secondhand.presentation.dto.OauthTokenResponse;
import kr.codesquad.secondhand.presentation.dto.member.AddressData;
import kr.codesquad.secondhand.presentation.dto.member.LoginRequest;
import kr.codesquad.secondhand.presentation.dto.member.LoginResponse;
import kr.codesquad.secondhand.presentation.dto.member.SignUpRequest;
import kr.codesquad.secondhand.presentation.dto.member.UserResponse;
import kr.codesquad.secondhand.presentation.dto.token.AuthToken;
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
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Transactional
    public LoginResponse login(OAuthProvider oAuthProvider, LoginRequest request, String code) {
        OAuthRequester oAuthRequester = oAuthProvider.getOAuthRequester();
        OauthTokenResponse tokenResponse = oAuthRequester.getToken(code);
        UserProfile userProfile = oAuthRequester.getUserProfile(tokenResponse);

        Member member = verifyUser(request, userProfile);
        Long memberId = member.getId();

        String refreshToken = jwtProvider.createRefreshToken(memberId);
        tokenRepository.deleteByMemberId(memberId);

        tokenRepository.save(RefreshToken.builder()
                .memberId(memberId)
                .token(refreshToken)
                .build());

        List<AddressData> addressData = residenceService.readResidenceOfMember(memberId);
        return new LoginResponse(
                new AuthToken(jwtProvider.createAccessToken(memberId), refreshToken),
                new UserResponse(member.getLoginId(), member.getProfileUrl(), addressData)
        );
    }

    @Transactional
    public void signUp(OAuthProvider oAuthProvider, SignUpRequest request, String code, MultipartFile profile) {
        verifyDuplicated(request);

        OAuthRequester oAuthRequester = oAuthProvider.getOAuthRequester();
        OauthTokenResponse tokenResponse = oAuthRequester.getToken(code);
        UserProfile userProfile = oAuthRequester.getUserProfile(tokenResponse);

        if (profile != null) {
            String profileUrl = imageService.uploadImage(profile);
            userProfile.changeProfileUrl(profileUrl);
        }

        Member savedMember = saveMember(request, userProfile);
        residenceService.saveResidence(request.getAddressIds(), savedMember);
    }

    private void verifyDuplicated(SignUpRequest request) {
        if (memberRepository.existsByLoginId(request.getLoginId())) {
            throw new DuplicatedException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, String refreshToken) {
        JwtExtractor.extract(request).ifPresent(token -> {
            Long expiration = jwtProvider.getExpiration(token);
            redisService.set(token, "logout", expiration);
        });
        tokenRepository.deleteByToken(refreshToken);
    }

    private Member verifyUser(LoginRequest request, UserProfile userProfile) {
        Member member = memberRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA));
        if (!member.isSameEmail(userProfile.getEmail())) {
            throw new UnAuthorizedException(ErrorCode.INVALID_LOGIN_DATA);
        }
        return member;
    }

    private Member saveMember(SignUpRequest request, UserProfile userProfile) {
        return memberRepository.save(request.toMemberEntity(userProfile));
    }
}
