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
    INVALID_FILE_EXTENSION("이미지 파일의 확장자는 png, jpg, jpeg, svg만 가능합니다."),
    UPLOAD_FAIL("이미지 업로드 실패"),

    // AUTH
    INVALID_LOGIN_DATA("로그인 정보가 일치하지 않습니다."),
    DUPLICATED_LOGIN_ID("중복된 아이디입니다."),

    // COMMON
    INVALID_PARAMETER("유효한 파라미터값이 아닙니다.");

    private final String message;
}
