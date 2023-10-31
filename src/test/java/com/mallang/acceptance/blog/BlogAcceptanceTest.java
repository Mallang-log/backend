package com.mallang.acceptance.blog;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.중복됨;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설_요청;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class BlogAcceptanceTest extends AcceptanceTest {

    @Test
    void 블로그를_개설한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");

        // when
        var 응답 = 블로그_개설_요청(말랑_세션_ID, "mallang-blog");

        // then
        응답_상태를_검증한다(응답, 생성됨);
        값이_존재한다(ID를_추출한다(응답));
    }

    @Test
    void 블로그_이름이_올바르지_않으면_예외() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");

        // when
        var 응답 = 블로그_개설_요청(말랑_세션_ID, "한글-이름");

        // then
        응답_상태를_검증한다(응답, 잘못된_요청);
    }

    @Test
    void 동일한_이름의_블로그가_이미_존재하면_예외() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 안말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("안말랑");
        블로그_개설_요청(말랑_세션_ID, "mallang-log");

        // when
        var 응답 = 블로그_개설_요청(안말랑_세션_ID, "mallang-log");

        // then
        응답_상태를_검증한다(응답, 중복됨);
    }

    @Test
    void 이미_개설한_블로그가_있는_경우_예외() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        블로그_개설_요청(말랑_세션_ID, "mallang-log");

        // when
        var 응답 = 블로그_개설_요청(말랑_세션_ID, "mallang-2");

        // then
        응답_상태를_검증한다(응답, 잘못된_요청);
    }
}
