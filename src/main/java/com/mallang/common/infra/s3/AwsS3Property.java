package com.mallang.common.infra.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsS3Property(
        String bucket,
        String imagePath,
        int presignedUrlExpiresMinutes
) {
}
