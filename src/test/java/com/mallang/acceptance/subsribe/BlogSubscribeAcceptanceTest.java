package com.mallang.acceptance.subsribe;

import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceTestHelper.블로그_개설;
import static com.mallang.acceptance.subsribe.BlogSubscribeAcceptanceSteps.블로그_구독_요청;
import static com.mallang.acceptance.subsribe.BlogSubscribeAcceptanceSteps.블로그_구독_취소_요청;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 구독 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class BlogSubscribeAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 동훈_블로그_ID;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-blog");
        동훈_블로그_ID = 블로그_개설(동훈_세션_ID, "donghun-blog");
    }

    @Nested
    class 블로그_구독_시 {

        @Test
        void 블로그를_구독한다() {
            // when
            var 응답 = 블로그_구독_요청(말랑_세션_ID, 동훈_블로그_ID);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 자신의_블로그를_구독하면_예외() {
            // when
            var 응답 = 블로그_구독_요청(말랑_세션_ID, 말랑_블로그_ID);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 이미_구독한_블로그라면_예외() {
            // given
            블로그_구독_요청(말랑_세션_ID, 동훈_블로그_ID);

            // when
            var 응답 = 블로그_구독_요청(말랑_세션_ID, 동훈_블로그_ID);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }
    }

    @Nested
    class 블로그_구독_취소_시 {

        @Test
        void 구독한_블로그를_구독_취소한다() {
            // given
            블로그_구독_요청(말랑_세션_ID, 동훈_블로그_ID);

            // when
            var 응답 = 블로그_구독_취소_요청(말랑_세션_ID, 동훈_블로그_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 구독하지_않은_블로그라면_예외() {
            // when
            var 응답 = 블로그_구독_취소_요청(말랑_세션_ID, 말랑_블로그_ID);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }
    }

    @Disabled("회원 정보 조회 기능 이후 구현")
    @Nested
    class 특정_회원이_구독중인_블로그_조회_시 {

        @Test
        void 조회된다() {
//            // given
//            var 블로그1주인_세션_ID = 회원가입과_로그인_후_세션_ID_반환("블로그1주인");
//            var 블로그2주인_세션_ID = 회원가입과_로그인_후_세션_ID_반환("블로그2주인");
//            var 구독자_세션_ID = 회원가입과_로그인_후_세션_ID_반환("구독자");
//            var 블로그1_ID = 블로그_개설(블로그1주인_세션_ID, "blog1");
//            var 블로그2_ID = 블로그_개설(블로그2주인_세션_ID, "blog2");
//            블로그_구독_요청(구독자_세션_ID, 블로그1_ID);
//            블로그_구독_요청(구독자_세션_ID, 블로그2_ID);
//
//            // when
//            given()
//                    .param("memberId", 구독자_세션_ID)

            // then
        }
    }

    @Disabled("회원 정보 조회 기능 이후 구현")
    @Nested
    class 특정_블로그를_구독중인_구독자_조회_시 {

        @Test
        void 조회된다() {
            // given
//            Long 주인_ID = memberServiceTestHelper.회원을_저장한다("주인");
//            Long 주인_블로그_ID = blogServiceTestHelper.블로그_개설(주인_ID, "owner-blog");
//            Long 구독자1_ID = memberServiceTestHelper.회원을_저장한다("구독자1");
//            Long 구독자2_ID = memberServiceTestHelper.회원을_저장한다("구독자2");
//            Long 구독자3_ID = memberServiceTestHelper.회원을_저장한다("구독자3");
//            Long 다른블로그주인_ID = memberServiceTestHelper.회원을_저장한다("다른블로그주인");
//            Long 다른_블로그_ID = blogServiceTestHelper.블로그_개설(다른블로그주인_ID, "other-blog");
//            Long 다른블로그구독자1_ID = memberServiceTestHelper.회원을_저장한다("다른블로그구독자1");
//            blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자1_ID, 주인_블로그_ID));
//            blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자2_ID, 주인_블로그_ID));
//            blogSubscribeService.subscribe(new BlogSubscribeCommand(구독자3_ID, 주인_블로그_ID));
//            blogSubscribeService.subscribe(new BlogSubscribeCommand(다른블로그구독자1_ID, 다른_블로그_ID));
//
            // when

            // then
        }
    }
}
