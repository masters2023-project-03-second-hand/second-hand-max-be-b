package kr.codesquad.secondhand.presentation.dto;

import kr.codesquad.secondhand.presentation.dto.token.AuthToken;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final AuthToken jwt;
    private final UserResponse user;

    public LoginResponse(AuthToken authToken, UserResponse user) {
        this.jwt = authToken;
        this.user = user;
    }
}
