package kr.codesquad.secondhand.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // JWT
    INVALID_AUTH_HEADER("Authorization 헤더의 정보가 유효하지 않습니다."),
    EXPIRED_TOKEN("만료된 토큰입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),

    // IMAGE
    INVALID_IMAGE("유효하지 않은 이미지 입니다."),
    INVALID_FILE_EXTENSION("잘못된 파일 확장자 입니다."),
    UPLOAD_FAIL("이미지 업로드 실패");

    private final String message;
}
