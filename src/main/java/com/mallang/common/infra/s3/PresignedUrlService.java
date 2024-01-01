package com.mallang.common.infra.s3;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Component
public class PresignedUrlService {

    private final S3Presigner.Builder presignerBuilder;
    private final AwsS3Property s3Property;

    public String create(String imageExtension) {
        String imageName = createImageName(imageExtension);
        return createPresignedUrl(imageName);
    }

    private String createImageName(String imageExtension) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + imageExtension;
    }

    private String createPresignedUrl(String imageName) {
        try (S3Presigner presigner = presignerBuilder.build()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(s3Property.bucket())
                    .key(s3Property.imagePath() + imageName)
                    .build();
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(s3Property.presignedUrlExpiresMinutes()))
                    .putObjectRequest(objectRequest)
                    .build();
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
}
