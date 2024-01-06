package com.mallang.reference.infrastructure.jsoup;

import com.mallang.reference.domain.UrlTitleMetaInfoFetcher;
import com.mallang.reference.exception.InvalidReferenceLinkUrlException;
import com.mallang.reference.exception.NotFoundReferenceLinkMetaTitleException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class JsoupUrlTitleMetaInfoFetcher implements UrlTitleMetaInfoFetcher {

    private final RestClient restClient;

    public JsoupUrlTitleMetaInfoFetcher(RestClient.Builder builder) {
        this.restClient = builder.build();
    }

    public String fetch(String url) {
        String response = getResponse(url);
        if (!StringUtils.hasText(response)) {
            throw new NotFoundReferenceLinkMetaTitleException();
        }
        String titleValue = extractTitleMetaInfo(response);
        if (titleValue.isEmpty()) {
            throw new NotFoundReferenceLinkMetaTitleException();
        }
        return titleValue;
    }

    private String extractTitleMetaInfo(String response) {
        Document document = Jsoup.parse(response);
        Elements ogTitleElements = document.select("meta[property=og:title]");
        if (!ogTitleElements.isEmpty()) {
            return ogTitleElements.attr("content");
        }
        return document.title();
    }

    private String getResponse(String url) {
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            throw new InvalidReferenceLinkUrlException("URL 에 문제가 있습니다. 다시 한번 확인해주세요.");
        }
    }
}

