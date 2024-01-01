package com.mallang.common.infra.s3.presentation;

import com.mallang.common.infra.s3.AwsS3Property;
import com.mallang.common.infra.s3.PresignedUrlClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/infra/aws/s3/presigned-url")
@RestController
public class PresignedUrlController {

    private final AwsS3Property awsS3Property;
    private final PresignedUrlClient presignedUrlClient;

    @PostMapping
    public ResponseEntity<CreatePresignedUrlResponse> createPresignedUrl(
            CreatePresignedUrlRequest request
    ) {
        String url = presignedUrlClient.create(awsS3Property.imagePath(), request.fileName());
        return ResponseEntity.ok(new CreatePresignedUrlResponse(url));
    }
}
