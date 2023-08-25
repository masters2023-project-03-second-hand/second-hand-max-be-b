package kr.codesquad.secondhand.presentation.dto.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRenewRequest {

    private String refreshToken;
}

