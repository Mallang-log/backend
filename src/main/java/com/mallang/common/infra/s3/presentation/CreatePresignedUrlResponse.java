package com.mallang.common.infra.s3.presentation;

public record CreatePresignedUrlResponse(
        String presignedUrl
) {
}
