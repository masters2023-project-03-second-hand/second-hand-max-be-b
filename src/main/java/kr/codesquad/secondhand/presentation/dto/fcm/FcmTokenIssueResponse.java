package kr.codesquad.secondhand.presentation.dto.fcm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class FcmTokenIssueResponse {

    private final String token;
}
