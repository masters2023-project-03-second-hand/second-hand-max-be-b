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
    DELETE_FAIL("이미지 삭제 실패"),

    // AUTH
    INVALID_LOGIN_DATA("로그인 정보가 일치하지 않습니다."),
    DUPLICATED_LOGIN_ID("중복된 아이디입니다."),
    NOT_LOGIN("로그인된 상태가 아닙니다."),
    UNAUTHORIZED("수정 권한이 없습니다."),
    OAUTH_PROVIDER_NOT_FOUND("제공하지 않는 OAuth 프로바이더입니다."),
    OAUTH_FAIL_REQUEST_TOKEN("토큰 요청에 실패했습니다."),

    // COMMON
    INVALID_PARAMETER("유효한 파라미터값이 아닙니다."),
    INVALID_REQUEST("유효한 요청이 아닙니다."),
    NOT_FOUND("페이지를 찾을 수 없습니다."),

    // FIREBASE
    FIREBASE_CONFIG_ERROR("Firebase 설정 파일을 읽어올 수 없습니다."),
    FCM_TOKEN_NOT_FOUND("FCM 토큰을 찾을 수 없습니다.");

    private final String message;
}
