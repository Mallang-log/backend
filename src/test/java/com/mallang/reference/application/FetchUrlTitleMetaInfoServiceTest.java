package com.mallang.reference.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.common.ServiceTest;
import com.mallang.reference.config.ReferenceLinkIntegrationTestConfig;
import com.mallang.reference.domain.MockUrlTitleMetaInfoFetcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@DisplayName("참고 링크의 제목 추출 서비스 (FetchUrlTitleMetaInfoService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Import({ReferenceLinkIntegrationTestConfig.class})
class FetchUrlTitleMetaInfoServiceTest extends ServiceTest {

    @Autowired
    private MockUrlTitleMetaInfoFetcher referenceLinkTitleFetcher;

    @Autowired
    private FetchUrlTitleMetaInfoService fetchUrlTitleMetaInfoService;

    @Test
    void 링크의_제목을_추출한다() {
        // given
        referenceLinkTitleFetcher.setResponse("title");

        // when
        String titleMetaInfo = fetchUrlTitleMetaInfoService.fetchTitleMetaInfo("url");

        // then
        assertThat(titleMetaInfo).isEqualTo("title");
    }
}
