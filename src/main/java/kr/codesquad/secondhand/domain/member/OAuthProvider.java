package kr.codesquad.secondhand.domain.member;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import kr.codesquad.secondhand.application.auth.KakaoRequester;
import kr.codesquad.secondhand.application.auth.NaverRequester;
import kr.codesquad.secondhand.application.auth.OAuthRequester;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
public enum OAuthProvider {

    KAKAO("kakao"),
    NAVER("naver");

    private final String name;
    private OAuthRequester oAuthRequester;

    public static OAuthProvider of(final String name) {
        return Arrays.stream(OAuthProvider.values())
                .filter(provider -> provider.name.equals(name))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.OAUTH_PROVIDER_NOT_FOUND));
    }

    private void injectOAuthClient(OAuthRequester oAuthRequester) {
        this.oAuthRequester = oAuthRequester;
    }

    @RequiredArgsConstructor
    @Component
    static class OAuthClientInjector {

        private final NaverRequester naverRequester;
        private final KakaoRequester kakaoRequester;

        @PostConstruct
        public void injectOAuthClient() {
            Arrays.stream(OAuthProvider.values()).forEach(oAuthProvider -> {
                if (oAuthProvider == NAVER) {
                    oAuthProvider.injectOAuthClient(naverRequester);
                }
                if (oAuthProvider == KAKAO) {
                    oAuthProvider.injectOAuthClient(kakaoRequester);
                }
            });
        }
    }
}
