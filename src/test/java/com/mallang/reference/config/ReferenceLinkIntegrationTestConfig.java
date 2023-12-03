package com.mallang.reference.config;

import com.mallang.reference.domain.MockUrlTitleMetaInfoFetcher;
import com.mallang.reference.domain.service.UrlTitleMetaInfoFetcher;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ReferenceLinkIntegrationTestConfig {

    @Primary
    @Bean
    public UrlTitleMetaInfoFetcher referenceLinkTitleFetcher() {
        return new MockUrlTitleMetaInfoFetcher();
    }
}
