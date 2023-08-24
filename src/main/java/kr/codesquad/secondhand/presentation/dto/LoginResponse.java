package kr.codesquad.secondhand.presentation.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final UserResponse user;

    public LoginResponse(UserResponse user) {
        this.user = user;
    }
}
