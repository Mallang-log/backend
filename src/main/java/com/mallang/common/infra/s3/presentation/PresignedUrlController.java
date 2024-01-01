package com.mallang.common.infra.s3.presentation;

import com.mallang.common.infra.s3.PresignedUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/infra/aws/s3/presigned-url")
@RestController
public class PresignedUrlController {

    private final PresignedUrlService presignedUrlService;

    @PostMapping
    public ResponseEntity<CreatePresignedUrlResponse> createPresignedUrl(
            CreatePresignedUrlRequest request
    ) {
        String url = presignedUrlService.create(request.imageExtension());
        return ResponseEntity.ok(new CreatePresignedUrlResponse(url));
    }
}
