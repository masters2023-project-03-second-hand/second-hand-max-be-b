package kr.codesquad.secondhand.presentation.dto;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private int statusCode;
    private T data;

    public ApiResponse(int statusCode, T data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public ApiResponse(int statusCode) {
        this.statusCode = statusCode;
    }
}
