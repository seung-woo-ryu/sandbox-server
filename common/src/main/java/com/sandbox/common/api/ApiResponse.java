package com.sandbox.common.api;

public record ApiResponse<T>(
        boolean success,
        T data,
        Error error,
        Meta meta
) {
    public record Error(
            String code,
            String message
    ) {
    }

    public record Meta(
            int totalCount, // 총 data 갯수
            int totalPageCount, // 총 페이지 갯수
            int size, // 한페이지에 보여질 갯수
            boolean hasNext // 다음 페이지 존재 여부
    ) {
    }
}
