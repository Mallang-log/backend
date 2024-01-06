package com.mallang.reference.application;

import com.mallang.reference.domain.UrlTitleMetaInfoFetcher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FetchUrlTitleMetaInfoService {

    private final UrlTitleMetaInfoFetcher fetcher;

    public String fetchTitleMetaInfo(String url) {
        return fetcher.fetch(url);
    }
}
