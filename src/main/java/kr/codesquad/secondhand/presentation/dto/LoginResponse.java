package kr.codesquad.secondhand.presentation.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private final String loginId;
    private final String profileUrl;

    public LoginResponse(String loginId, String profileUrl) {
        this.loginId = loginId;
        this.profileUrl = profileUrl;
    }
}
