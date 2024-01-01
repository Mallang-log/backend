package com.mallang.acceptance.auth;

import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.인증되지_않음;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.로그아웃_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.아이디_중복_체크_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.일반_로그인_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.일반_회원가입_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과_데이터;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과를_검증한다;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_요청;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.presentation.request.BasicSignupRequest;
import com.mallang.auth.presentation.response.CheckDuplicateResponse;
import com.mallang.auth.query.response.MemberResponse;
import org.junit.jupiter.api.BeforeEach;
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
    class 아이디_중복_체크_API {

        @Test
        void 주어진_아이디가_이미_존재하면_중복됨() {
            // given
            일반_회원가입_요청(회원가입_요청);

            // when
            var 응답 = 아이디_중복_체크_요청(아이디);

            // then
            CheckDuplicateResponse response = 응답.as(CheckDuplicateResponse.class);
            assertThat(response.duplicated()).isTrue();
        }

        @Test
        void 주어진_아이디가_존재하지_않으면_중복되지_않음() {
            // when
            var 응답 = 아이디_중복_체크_요청(아이디);

            // then
            CheckDuplicateResponse response = 응답.as(CheckDuplicateResponse.class);
            assertThat(response.duplicated()).isFalse();
        }
    }

    @Nested
    class 회원가입_API {

        @Test
        void 회원가입한다() {
            // when
            var 응답 = 일반_회원가입_요청(회원가입_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
        }

        @Test
        void 아이디가_중복되면_예외() {
            // given
            일반_회원가입_요청(회원가입_요청);

            // when
            var 응답 = 일반_회원가입_요청(회원가입_요청);

            // then
            응답_상태를_검증한다(응답, 중복됨);
        }
    }

    @Nested
    class 로그인_API {

        @BeforeEach
        void setUp() {
            일반_회원가입_요청(회원가입_요청);
        }

        @Test
        void 로그인한다() {
            // when
            var 응답 = 일반_로그인_요청(아이디, 비밀번호);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
            assertThat(응답.cookie("JSESSIONID")).isNotNull();
        }

        @Test
        void 아이디가_없으면_예외() {
            // when
            var 응답 = 일반_로그인_요청(아이디 + "wrong", 비밀번호);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
            assertThat(응답.cookie("JSESSIONID")).isNull();
        }

        @Test
        void 비밀번호가_없으면_예외() {
            // when
            var 응답 = 일반_로그인_요청(아이디, 비밀번호 + "wrong");

            // then
            응답_상태를_검증한다(응답, 인증되지_않음);
            assertThat(응답.cookie("JSESSIONID")).isNull();
        }
    }


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
