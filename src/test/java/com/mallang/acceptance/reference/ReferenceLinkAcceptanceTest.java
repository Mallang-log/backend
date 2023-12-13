package com.mallang.acceptance.reference;


import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.인증되지_않음;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.reference.LabelAcceptanceSteps.라벨_생성_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.URL_의_제목_추출_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.주어진_URL_로_이미_등록된_링크_존재여부_확인_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.참고_링크_검색_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.참고_링크_삭제_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.참고_링크_업데이트_요청;
import static com.mallang.acceptance.reference.ReferenceLinkAcceptanceSteps.참고_링크_저장_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.reference.config.ReferenceLinkIntegrationTestConfig;
import com.mallang.reference.domain.MockUrlTitleMetaInfoFetcher;
import com.mallang.reference.exception.InvalidReferenceLinkUrlException;
import com.mallang.reference.exception.NotFoundReferenceLinkMetaTitleException;
import com.mallang.reference.presentation.request.CreateLabelRequest;
import com.mallang.reference.presentation.request.SaveReferenceLinkRequest;
import com.mallang.reference.presentation.request.UpdateReferenceLinkRequest;
import com.mallang.reference.query.repository.ReferenceLinkSearchDao.ReferenceLinkSearchDaoCond;
import com.mallang.reference.query.response.ReferenceLinkSearchResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
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
    private Long 말랑_라벨_ID;
    private String 동훈_세션;
    private Long 동훈_라벨_ID;
    private SaveReferenceLinkRequest 참고_링크_저장_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션 = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션 = 회원가입과_로그인_후_세션_ID_반환("동훈");
        var 라벨_생성_요청 = new CreateLabelRequest(
                "label",
                "#000000",
                null,
                null
        );
        말랑_라벨_ID = ID를_추출한다(라벨_생성_요청(말랑_세션, 라벨_생성_요청));
        동훈_라벨_ID = ID를_추출한다(라벨_생성_요청(동훈_세션, 라벨_생성_요청));
        참고_링크_저장_요청 = new SaveReferenceLinkRequest(
                "https://ttl-blog.tistory.com",
                "말링이 블로그",
                "말랑이 블로그임",
                말랑_라벨_ID
        );
    }

    @Nested
    class 참고_링크_저장_API {

        @Test
        void 인증정보가_없으면_아니라면_예외() {
            // when
            var 응답 = 참고_링크_저장_요청(null, 참고_링크_저장_요청);

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }

        @Test
        void 참고_링크를_저장한다() {
            // when
            var 응답 = 참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 타입의_라벨을_설정히면_예외() {
            // given
            var 참고_링크_저장_요청 = new SaveReferenceLinkRequest(
                    "https://ttl-blog.tistory.com",
                    "말링이 블로그",
                    "말랑이 블로그임",
                    동훈_라벨_ID
            );

            // when
            var 응답 = 참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 참고_링크_정보_업데이트_API {

        private final UpdateReferenceLinkRequest 참고_링크_업데이트_요청 = new UpdateReferenceLinkRequest(
                "https://donghun.com",
                "동훈이 블로그",
                "동훈이 블로그임",
                null
        );

        @Test
        void 참고_링크를_정보를_수정한다() {
            // given
            var 참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청));

            // when
            var 응답 = 참고_링크_업데이트_요청(말랑_세션, 참고_링크_ID, 참고_링크_업데이트_요청);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신이_등록한_링크가_아니라면_예외() {
            // given
            var 다른회원_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            var 참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청));

            // when
            var 응답 = 참고_링크_업데이트_요청(다른회원_세션_ID, 참고_링크_ID, 참고_링크_업데이트_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 타입의_라벨을_설정히면_예외() {
            // given
            var 참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청));
            var 참고_링크_업데이트_요청 = new UpdateReferenceLinkRequest(
                    "https://ttl-blog.tistory.com",
                    "말링이 블로그",
                    "말랑이 블로그임",
                    동훈_라벨_ID
            );

            // when
            var 응답 = 참고_링크_업데이트_요청(말랑_세션, 참고_링크_ID, 참고_링크_업데이트_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 참고_링크_삭제_API {

        @Test
        void 참고_링크를_저장한다() {
            // given
            var 참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청));

            // when
            var 응답 = 참고_링크_삭제_요청(말랑_세션, 참고_링크_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 자신이_등록한_링크가_아니라면_예외() {
            // given
            var 다른회원_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            var 참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(말랑_세션, 참고_링크_저장_요청));

            // when
            var 응답 = 참고_링크_삭제_요청(다른회원_세션_ID, 참고_링크_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 주어진_URL_로_이미_등록된_링크_존재여부_확인_API {

        @Test
        void Url이_정확히_일치해야_일치하는_것이다() {
            // given
            참고_링크_저장_요청(
                    말랑_세션,
                    new SaveReferenceLinkRequest(
                            "https://ttl-blog.tistory.com",
                            "말랑이 블로그",
                            "말랑이 블로그 메인 페이지이다.",
                            null
                    )
            );

            // when
            var exactlyMatch = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    말랑_세션,

                    "https://ttl-blog.tistory.com"
            );
            var exactlyMatch2 = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    말랑_세션,

                    " https://ttl-blog.tistory.com "  // 앞뒤 공백은 제거됨
            );
            var notMatch1 = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    말랑_세션,

                    "//ttl-blog.tistory.com"
            );
            var notMatch2 = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    말랑_세션,

                    "https://ttl-blog.tistory.com/"
            );

            // then
            assertThat(exactlyMatch.as(Boolean.class)).isTrue();
            assertThat(exactlyMatch2.as(Boolean.class)).isTrue();
            assertThat(notMatch1.as(Boolean.class)).isFalse();
            assertThat(notMatch2.as(Boolean.class)).isFalse();
        }

        @Test
        void 다른_사람이_등록한것과는_무관하다() {
            // given
            var 다른_사람_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            참고_링크_저장_요청(
                    다른_사람_세션_ID,
                    new SaveReferenceLinkRequest(
                            "https://ttl-blog.tistory.com",
                            "말랑이 블로그",
                            "말랑이 블로그 메인 페이지이다.",
                            null
                    )
            );

            // when
            var notExist = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    말랑_세션,

                    "https://ttl-blog.tistory.com"
            );
            var exist = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    다른_사람_세션_ID,
                    "https://ttl-blog.tistory.com"
            );

            // then
            assertThat(exist.as(Boolean.class)).isTrue();
            assertThat(notExist.as(Boolean.class)).isFalse();
        }

        @Test
        void 인증정보가_없으면_예외() {
            // given

            // when
            var 응답 = 주어진_URL_로_이미_등록된_링크_존재여부_확인_요청(
                    null,
                    "https://ttl-blog.tistory.com"
            );

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }
    }

    @Nested
    class 참고_링크_목록_검색_API {

        private Long 말랑이_블로그_링크_ID;
        private Long Spring_글_참고_링크_ID;

        @BeforeEach
        void setUp() {
            말랑이_블로그_링크_ID = ID를_추출한다(참고_링크_저장_요청(
                    말랑_세션,

                    new SaveReferenceLinkRequest(
                            "https://ttl-blog.tistory.com",
                            "말랑이 블로그",
                            "말랑이 블로그 메인 페이지이다.",
                            null
                    )
            ));
            Spring_글_참고_링크_ID = ID를_추출한다(참고_링크_저장_요청(
                    말랑_세션,

                    new SaveReferenceLinkRequest(
                            "https://ttl-blog.tistory.com/123",
                            "스프링이란?",
                            "말랑이가 쓴 스프링에 대한 내용.",
                            null
                    )
            ));
        }

        @Test
        void 조건_없이_검색하면_나의_모든_링크가_조회된다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when
            var 응답 = 참고_링크_검색_요청(말랑_세션, emptyCond);

            // then
            List<ReferenceLinkSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses).hasSize(2);
        }

        @Test
        void 다른_사람의_링크는_조회되지_않는다() {
            // given
            var 다른_사람_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            Long 다른사람_링크_ID = ID를_추출한다(참고_링크_저장_요청(
                    다른_사람_세션_ID,
                    new SaveReferenceLinkRequest(
                            "https://ttl-blog.tistory.com/13",
                            "자바",
                            "말랑이가 쓴 자바에 대한 내용.",
                            null
                    )
            ));
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when
            var 응답 = 참고_링크_검색_요청(말랑_세션, emptyCond);

            // then
            List<ReferenceLinkSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .doesNotContain(다른사람_링크_ID);
        }

        @Test
        void 인증정보가_없으면_조회할_수_없다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, null);

            // when
            var 응답 = 참고_링크_검색_요청(null, emptyCond);

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
        }

        @Test
        void Url_포함조건으로_검색한다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond("12", null, null);

            // when
            var 응답 = 참고_링크_검색_요청(말랑_세션, emptyCond);

            // then
            List<ReferenceLinkSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }

        @Test
        void 제목_포함조건으로_검색한다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, "랑이", null);

            // when
            var 응답 = 참고_링크_검색_요청(말랑_세션, emptyCond);

            // then
            List<ReferenceLinkSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(말랑이_블로그_링크_ID);
        }

        @Test
        void 메모_검색조건으로_검색한다() {
            // given
            ReferenceLinkSearchDaoCond emptyCond = new ReferenceLinkSearchDaoCond(null, null, "스프링에");

            // when
            var 응답 = 참고_링크_검색_요청(말랑_세션, emptyCond);

            // then
            List<ReferenceLinkSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .extracting(ReferenceLinkSearchResponse::referenceLinkId)
                    .containsExactly(Spring_글_참고_링크_ID);
        }
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
