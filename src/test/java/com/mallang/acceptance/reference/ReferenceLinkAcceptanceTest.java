package com.mallang.acceptance.reference;


import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.인증되지_않음;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.URL_의_제목_추출_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.reference.config.ReferenceLinkIntegrationTestConfig;
import com.mallang.reference.domain.MockUrlTitleMetaInfoFetcher;
import com.mallang.reference.exception.InvalidReferenceLinkUrlException;
import com.mallang.reference.exception.NotFoundReferenceLinkMetaTitleException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@DisplayName("참고 링크 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Import({ReferenceLinkIntegrationTestConfig.class})
public class ReferenceLinkAcceptanceTest extends AcceptanceTest {

    @Autowired
    private MockUrlTitleMetaInfoFetcher fetcher;
    private String 말랑_세션;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션 = 회원가입과_로그인_후_세션_ID_반환("말랑");
    }


    @Nested
    class url의_제목_정보_추출_API {

        private final String url = "https://ttl-blog-tistory.com";

        @Test
        void url로부터_제목_정보를_추출한다() {
            // given
            fetcher.setResponse("title");

            // when
            var 응답 = URL_의_제목_추출_요청(말랑_세션, url);

            // then
            assertThat(응답.body().asString()).isEqualTo("title");
        }

        @Test
        void 로그인되지_않으면_예외() {
            // when
            var 응답 = URL_의_제목_추출_요청(null, url);

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }

        @Test
        void 제목_정보를_찾을_수_없다면_예외() {
            // given
            fetcher.setException(new NotFoundReferenceLinkMetaTitleException());

            // when
            var 응답 = URL_의_제목_추출_요청(말랑_세션, url);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void url_에_문자가_있다면_예외() {
            // given
            fetcher.setException(new InvalidReferenceLinkUrlException("url에 문제가 있습니다."));

            // when
            var 응답 = URL_의_제목_추출_요청(말랑_세션, url);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }
    }
}
