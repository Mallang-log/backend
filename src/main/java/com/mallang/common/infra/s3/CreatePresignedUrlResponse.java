package com.mallang.common.infra.s3;

public record CreatePresignedUrlResponse(
        String imageName,
        String presignedUrl
) {
}
