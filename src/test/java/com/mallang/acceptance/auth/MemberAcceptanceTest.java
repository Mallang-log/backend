package com.mallang.acceptance.auth;

import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.로그아웃_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과_데이터;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과를_검증한다;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.presentation.request.BasicSignupRequest;
import com.mallang.auth.query.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("회원 인수테스트 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MemberAcceptanceTest extends AcceptanceTest {

    private final String 아이디 = "mallang";
    private final String 비밀번호 = "password";
    private final BasicSignupRequest 회원가입_요청 = new BasicSignupRequest(
            아이디,
            비밀번호,
            "mallang",
            "profile"
    );

    @Nested
    class 로그아웃_API {

        @Test
        void 로그아웃한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("mallang");

            // when
            var 응답 = 로그아웃_요청(말랑_세션_ID);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            assertThat(응답.header("Set-Cookie")).contains("Max-Age=0");
        }
    }

    @Nested
    class 내_정보_조회_API {

        @Test
        void 인증되었다면_조회된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("mallang");

            // when
            var 응답 = 내_정보_조회_요청(말랑_세션_ID);

            // then
            회원_정보_조회_결과를_검증한다(응답, new MemberResponse(null, "mallang", "mallang"));
        }
    }

    @Nested
    class 회원_정보_조회_API {

        @Test
        void 회원_정보를_조회한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("mallang");
            var 회원정보 = 회원_정보_조회_결과_데이터(내_정보_조회_요청(말랑_세션_ID));

            // when
            var 응답 = 회원_정보_조회_요청(회원정보.id());

            // then
            회원_정보_조회_결과를_검증한다(응답, 회원정보);
        }
    }
}
