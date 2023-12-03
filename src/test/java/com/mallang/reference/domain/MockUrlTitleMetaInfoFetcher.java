package com.mallang.reference.domain;

import com.mallang.reference.domain.service.UrlTitleMetaInfoFetcher;

public class MockUrlTitleMetaInfoFetcher implements UrlTitleMetaInfoFetcher {

    private String title;

    public void setResponse(String title) {
        this.title = title;
    }

    @Override
    public String fetch(String url) {
        return title;
    }
}
