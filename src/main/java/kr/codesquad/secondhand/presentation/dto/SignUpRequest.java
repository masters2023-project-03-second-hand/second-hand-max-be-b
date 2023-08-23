package kr.codesquad.secondhand.presentation.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min = 2, max = 12)
    private String loginId;
    @NotBlank
    private String addrName;

    public SignUpRequest(String loginId, String addrName) {
        this.loginId = loginId;
        this.addrName = addrName;
    }
}
