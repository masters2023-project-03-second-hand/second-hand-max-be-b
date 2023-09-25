package kr.codesquad.secondhand.presentation.dto.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModifyProfileResponse {

    private final String profileImageUrl;
}
