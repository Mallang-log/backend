package com.mallang.reference.infrastructure.jsoup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.mallang.reference.domain.service.UrlTitleMetaInfoFetcher;
import com.mallang.reference.exception.InvalidReferenceLinkUrlException;
import com.mallang.reference.exception.NotFoundReferenceLinkMetaTitleException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * Test code reference :
 * https://github.com/spring-projects/spring-boot/blob/main/spring-boot-project/spring-boot-test-autoconfigure/src/test/java/org/springframework/boot/test/autoconfigure/web/client/RestClientTestWithRestClientComponentIntegrationTests.java
 */
@DisplayName("Url로부터 제목 정보 추출기 (JsoupUrlTitleMetaInfoFetcherTest) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@RestClientTest({UrlTitleMetaInfoFetcher.class})
class JsoupUrlTitleMetaInfoFetcherTest {

    @Autowired
    private UrlTitleMetaInfoFetcher fetcher;

    @Autowired
    private MockRestServiceServer mockServer;

    private final String url = "https://sample-link";

    @ParameterizedTest
    @ValueSource(strings = {
            """
                    <!DOCTYPE html>
                    <head>
                        <title>
                              Shin._.Mallang
                        </title>
                     </head>
                                            
                    <body>
                        HI
                    </body>
                                            
                    </html>
                    """,
            """
                    <!DOCTYPE html>
                    <head>
                        <title>
                            title-value
                        </title>
                        <meta property="og:title" content="Shin._.Mallang"/>
                     </head>
                                            
                    <body>
                        HI
                    </body>
                                            
                    </html>
                    """

    })
    void Url의_응답에서_오픈그래프_title_메타태그_혹은_title_태그의_값을_반환한다_우선순위는_오픈그래프가_더_높다(String html) {
        // given
        mockServer.expect(requestTo(url))
                .andRespond(withSuccess(html, TEXT_PLAIN));

        // when
        String fetch = fetcher.fetch(url);

        // then
        assertThat(fetch).isEqualTo("Shin._.Mallang");
    }

    @Test
    void title_이나_og_title이_없다면_예외() {
        // given
        mockServer.expect(requestTo(url))
                .andRespond(withSuccess("""
                        <!DOCTYPE html>
                        <head>
                            <meta property="og:site_name" content="sample"/>
                         </head>
                                                
                        <body>
                            HI
                        </body>
                                                
                        </html>
                        """, TEXT_PLAIN));

        // when & then
        assertThatThrownBy(() ->
                fetcher.fetch(url)
        ).isInstanceOf(NotFoundReferenceLinkMetaTitleException.class);
    }

    @Test
    void 태그는_존재하지만_태그에_값이_없다면_빈_값_반환() {
        // given
        mockServer.expect(requestTo(url))
                .andRespond(withSuccess("""
                        <!DOCTYPE html>
                        <head>
                            <meta property="og:title""/>
                         </head>
                                                
                        <body>
                            HI
                        </body>
                                                
                        </html>
                        """, TEXT_PLAIN));

        // when & then
        assertThatThrownBy(() ->
                fetcher.fetch(url)
        ).isInstanceOf(NotFoundReferenceLinkMetaTitleException.class);
    }

    @Test
    void body가_없다면_예외() {
        // given
        mockServer.expect(requestTo(url))
                .andRespond(withSuccess());

        // when & then
        assertThatThrownBy(() ->
                fetcher.fetch(url)
        ).isInstanceOf(NotFoundReferenceLinkMetaTitleException.class);
    }
}

@SpringBootTest
class RealRequestTest {

    @Autowired
    private UrlTitleMetaInfoFetcher fetcher;

    @Test
    void Url_이_올바르지_않다면_예외() {
        // when & then
        assertThatThrownBy(() -> {
            fetcher.fetch("https://nonexist.dsqadwqkodjwqoidwqdwqlkdjwqdwqdwq.dwqdwqdwqdwq.com");
        }).isInstanceOf(InvalidReferenceLinkUrlException.class);
        assertThatThrownBy(() -> {
            fetcher.fetch("/test");
        }).isInstanceOf(InvalidReferenceLinkUrlException.class);
    }

    @Disabled("URL이 바뀌거나 해당 사이트의 정보가 바뀌면 깨질 수 있으므로 Disabled")
    @Test
    void 추출_성공_테스트() {
        // given
        String url = "https://ttl-blog.tistory.com/";  // URL 이 바뀌거나 내용이 달라지면 실패할 수 있음

        // when
        String fetch = fetcher.fetch(url);

        // then
        assertThat(fetch).isEqualTo("Shin._.Mallang");
    }
}
