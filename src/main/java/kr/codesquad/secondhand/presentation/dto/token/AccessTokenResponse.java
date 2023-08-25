package kr.codesquad.secondhand.presentation.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccessTokenResponse {

    private final String accessToken;
}
