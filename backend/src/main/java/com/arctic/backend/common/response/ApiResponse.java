package com.arctic.backend.common.response;

public record ApiResponse<T>(
        int status,
        boolean success,
        T data
) {


    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, true, data);
    }


    public static <T> ApiResponse<T> success(int status, T data) {
        return new ApiResponse<>(status, true, data);
    }

    public static <T> ApiResponse<T> success(int status, boolean isSuccess, T data) {
        return new ApiResponse<>(status, isSuccess, data);
    }

    public static <T> ApiResponse<T> success(int status, boolean isSuccess) {
        return new ApiResponse<>(status, true, null);
    }

}