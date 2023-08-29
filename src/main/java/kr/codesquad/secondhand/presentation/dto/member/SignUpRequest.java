package kr.codesquad.secondhand.presentation.dto.member;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min = 2, max = 12, message = "아이디는 2자 ~ 12자여야 합니다.")
    private String loginId;
    @NotBlank
    private String addrName;

    public SignUpRequest(String loginId, String addrName) {
        this.loginId = loginId;
        this.addrName = addrName;
    }
}
