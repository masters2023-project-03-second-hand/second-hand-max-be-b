package kr.codesquad.secondhand.presentation.dto.token;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutRequest {

    @NotBlank(message = "토큰 값은 비어 있을 수 없습니다.")
    private String refreshToken;
}
