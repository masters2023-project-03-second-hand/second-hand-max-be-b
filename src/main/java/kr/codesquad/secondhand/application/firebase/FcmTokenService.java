package kr.codesquad.secondhand.application.firebase;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import kr.codesquad.secondhand.application.redis.RedisService;
import kr.codesquad.secondhand.exception.ErrorCode;
import kr.codesquad.secondhand.exception.InternalServerException;
import kr.codesquad.secondhand.infrastructure.properties.FcmProperties;
import kr.codesquad.secondhand.presentation.dto.fcm.FcmTokenIssueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.FileInputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class FcmTokenService {

    private static final String FCM_TOKEN_PREFIX = "fcm_token:";

    private final FcmProperties fcmProperties;
    private final RedisService redisService;

    public void updateToken(String token, Long memberId) {
        redisService.set(FCM_TOKEN_PREFIX + memberId, token, fcmProperties.getExpirationMillis());
    }

    public FcmTokenIssueResponse issueToken() {
        try (FileInputStream serviceAccount = new FileInputStream(fcmProperties.getPrivateKeyPath())) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount)
                    .createScoped(fcmProperties.getScopes());

            AccessToken accessToken = credentials.refreshAccessToken();

            return new FcmTokenIssueResponse(accessToken.getTokenValue());
        } catch (IOException e) {
            throw new InternalServerException(ErrorCode.FIREBASE_CONFIG_ERROR, "FCM 토큰 발급에 실패했습니다.");
        }
    }
}
