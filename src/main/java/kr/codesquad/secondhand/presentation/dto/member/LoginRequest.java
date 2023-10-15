package kr.codesquad.secondhand.presentation.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequest {

    @NotBlank
    @Size(min = 2, max = 12, message = "아이디는 2자 ~ 12자여야 합니다.") // todo: ExceptionHandler에서 예외처리하기
    private String loginId;

    public LoginRequest(String loginId) {
        this.loginId = loginId;
    }
}
