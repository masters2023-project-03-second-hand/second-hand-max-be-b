package kr.codesquad.secondhand.presentation.dto.fcm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class FcmTokenUpdateRequest {

    @NotEmpty(message = "토큰은 필수 입력 값입니다.")
    private String token;
}
