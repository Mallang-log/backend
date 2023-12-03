package com.mallang.reference.presentation;

import com.mallang.reference.application.FetchUrlTitleMetaInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/reference-links")
@RestController
public class ReferenceLinkController {

    private final FetchUrlTitleMetaInfoService fetchReferenceLinkTitleService;

    @GetMapping("/title-info")
    public ResponseEntity<String> fetchTitleInfo(
            @RequestParam("url") String url
    ) {
        return ResponseEntity.ok(fetchReferenceLinkTitleService.fetchTitleMetaInfo(url));
    }
}
