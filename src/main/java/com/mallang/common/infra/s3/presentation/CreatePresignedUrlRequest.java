package com.mallang.common.infra.s3.presentation;

public record CreatePresignedUrlRequest(
        String imageExtension
) {
}
