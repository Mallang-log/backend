package com.mallang.common.infra.s3;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@RequiredArgsConstructor
@Component
public class PresignedUrlService {

    private final S3Presigner.Builder presignerBuilder;
    private final AwsS3Property s3Property;

    public CreatePresignedUrlResponse create(String imageExtension) {
        String imageName = createImageName(imageExtension);
        String presignedUrl = createPresignedUrl(imageName);
        return new CreatePresignedUrlResponse(imageName, presignedUrl);
    }

    private String createImageName(String imageExtension) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + imageExtension;
    }

    private String createPresignedUrl(String imageName) {
        try (S3Presigner presigner = presignerBuilder.build()) {
            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(s3Property.presignedUrlExpiresMinutes()))
                    .putObjectRequest(builder -> builder
                            .bucket(s3Property.bucket())
                            .key(s3Property.imagePath() + imageName)
                            .build()
                    ).build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toExternalForm();
        }
    }
}
