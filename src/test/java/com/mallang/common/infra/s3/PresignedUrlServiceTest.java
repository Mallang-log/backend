package com.mallang.common.infra.s3;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;


@Testcontainers
@DisplayName("프리사인즈 URL 서비스 (PresignedUrlService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PresignedUrlServiceTest {

    @Container
    public LocalStackContainer localStackContainer = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack"))
            .withServices(LocalStackContainer.Service.S3);

    @Test
    void 이미지_확장자를_받아_이미지_이름을_UUID로_생성_후_프리사인드_URL을_생성하여_반환한다() {
        // given
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                localStackContainer.getAccessKey(),
                localStackContainer.getSecretKey()
        );
        Builder presignerBuilder = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(localStackContainer.getRegion()));
        PresignedUrlService service = new PresignedUrlService(
                presignerBuilder,
                new AwsS3Property("mallang-bucket", "images/", 10)
        );

        // when
        CreatePresignedUrlResponse response = service.create("img");

        // then
        assertThat(response.presignedUrl()).contains(
                "https://mallang-bucket.s3.amazonaws.com/",
                "images/" + response.imageName(),
                ".img",
                "X-Amz-Expires="
        );
    }
}
