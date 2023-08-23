package kr.codesquad.secondhand.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    private String loginId;

    public LoginRequest(String loginId) {
        this.loginId = loginId;
    }
}
