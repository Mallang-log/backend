package com.mallang.acceptance.common.s3;

import com.mallang.common.infra.s3.AwsS3Property;
import com.mallang.common.infra.s3.PresignedUrlClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

@ActiveProfiles("test")
@Primary
@Component
public class MockPresignedUrlClient extends PresignedUrlClient {

    public MockPresignedUrlClient(
            Builder presignerBuilder,
            AwsS3Property s3Property
    ) {
        super(presignerBuilder, s3Property);
    }

    @Override
    public String create(String path, String fileName) {
        return "https://example/" + path + fileName;
    }
}
