package kr.codesquad.secondhand.presentation.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private int statusCode;
    private String message;
    private T data;

    public ApiResponse(int statusCode, T data) {
        this.statusCode = statusCode;
        this.message = "성공했습니다.";
        this.data = data;
    }

    public ApiResponse(int statusCode) {
        this(statusCode, null);
    }
}
