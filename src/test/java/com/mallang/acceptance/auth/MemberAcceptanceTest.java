package com.mallang.acceptance.auth;

import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.내_정보_조회_요청;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과_데이터;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_결과를_검증한다;
import static com.mallang.acceptance.auth.MemberAcceptanceSteps.회원_정보_조회_요청;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.auth.query.data.MemberProfileData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("회원 인수테스트 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class MemberAcceptanceTest extends AcceptanceTest {

    @Nested
    class 내_정보_조회_시 {

        @Test
        void 인증되었다면_조회된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("mallang");

            // when
            var 응답 = 내_정보_조회_요청(말랑_세션_ID);

            // then
            회원_정보_조회_결과를_검증한다(응답, new MemberProfileData(null, "mallang", "mallang"));
        }

        @Nested
        class 회원_정보_조회_시 {

            @Test
            void 회원_정보를_조회한다() {
                // given
                var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("mallang");
                var 정보 = 회원_정보_조회_결과_데이터(내_정보_조회_요청(말랑_세션_ID));

                // when
                var 응답 = 회원_정보_조회_요청(정보.id());

                // then
                회원_정보_조회_결과를_검증한다(응답, 정보);
            }
        }
    }
}
