package com.mallang.reference.domain;

import com.mallang.common.execption.MallangLogException;

public class MockUrlTitleMetaInfoFetcher implements UrlTitleMetaInfoFetcher {

    private String title;
    private MallangLogException throwable;

    public void setResponse(String title) {
        this.title = title;
    }

    public void setException(MallangLogException exception) {
        this.throwable = exception;
    }

    @Override
    public String fetch(String url) {
        if (throwable != null) {
            MallangLogException temp = throwable;
            this.throwable = null;
            throw temp;
        }
        return title;
    }
}
