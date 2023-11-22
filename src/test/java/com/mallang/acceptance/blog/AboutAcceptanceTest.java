package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.given;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.AboutAcceptanceSteps.블로그_소개_삭제_요청;
import static com.mallang.acceptance.blog.AboutAcceptanceSteps.블로그_소개_수정_요청;
import static com.mallang.acceptance.blog.AboutAcceptanceSteps.블로그_소개_작성_요청;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.blog.presentation.request.DeleteAboutRequest;
import com.mallang.blog.presentation.request.UpdateAboutRequest;
import com.mallang.blog.presentation.request.WriteAboutRequest;
import com.mallang.blog.query.response.AboutResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("소개(About) 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class AboutAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private WriteAboutRequest 말랑_블로그_소개_작성_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        말랑_블로그_소개_작성_요청 = new WriteAboutRequest(말랑_블로그_ID, "말랑입니다.");
    }

    @Nested
    class 소개_작성_API {

        @Test
        void 첫_작성이라면_작성된다() {
            // when
            var 응답 = 블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 블로그에_이미_작성된_소개가_있으면_예외() {
            // given
            블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청);

            // when
            var 응답 = 블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }

        @Test
        void 다른_사람의_블로그에_작성하면_예외() {
            // when
            var 응답 = 블로그_소개_작성_요청(동훈_세션_ID, 말랑_블로그_소개_작성_요청);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }

    @Nested
    class 소개_수정_API {

        private Long 말랑_블로그_소개_ID;
        private UpdateAboutRequest 말랑_블로그_소개_수정_요청;

        @BeforeEach
        void setUp() {
            말랑_블로그_소개_ID = ID를_추출한다(블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청));
            말랑_블로그_소개_수정_요청 = new UpdateAboutRequest(말랑_블로그_ID, "수정입니다.");
        }

        @Test
        void 자신의_소개라면_수정된다() {
            // given
            블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청);

            // when
            var 응답 = 블로그_소개_수정_요청(말랑_세션_ID, 말랑_블로그_소개_ID, 말랑_블로그_소개_수정_요청);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // given
            블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청);

            // when
            var 응답 = 블로그_소개_수정_요청(동훈_세션_ID, 말랑_블로그_소개_ID, 말랑_블로그_소개_수정_요청);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }

    @Nested
    class 소개_삭제_API {

        private Long 말랑_블로그_소개_ID;
        private DeleteAboutRequest 말랑_블로그_소개_삭제_요청;

        @BeforeEach
        void setUp() {
            말랑_블로그_소개_ID = ID를_추출한다(블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청));
            말랑_블로그_소개_삭제_요청 = new DeleteAboutRequest(말랑_블로그_ID);
        }

        @Test
        void 자신의_소개라면_삭제된다() {
            // when
            var 응답 = 블로그_소개_삭제_요청(말랑_세션_ID, 말랑_블로그_소개_ID, 말랑_블로그_소개_삭제_요청);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // when
            var 응답 = 블로그_소개_삭제_요청(동훈_세션_ID, 말랑_블로그_소개_ID, 말랑_블로그_소개_삭제_요청);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }

    @Nested
    class 소개_조회_API {

        @Test
        void 소개를_조회한다() {
            // given
            var 소개_ID = ID를_추출한다(블로그_소개_작성_요청(말랑_세션_ID, 말랑_블로그_소개_작성_요청));

            // when
            var 응답 = given()
                    .queryParam("blogId", 말랑_블로그_ID)
                    .get("/abouts")
                    .then().log().all()
                    .extract();

            // then
            AboutResponse aboutResponse = 응답.as(AboutResponse.class);
            assertThat(aboutResponse.id()).isEqualTo(소개_ID);
        }
    }
}
