package com.mallang.acceptance.category;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성_요청;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class CategoryAcceptanceTest extends AcceptanceTest {

    @Test
    void 카테고리를_생성한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");

        // when
        var 응답 = 카테고리_생성_요청(말랑_세션_ID, "Spring", 없음());

        // then
        응답_상태를_검증한다(응답, 생성됨);
        값이_존재한다(ID를_추출한다(응답));
    }

    @Test
    void 하위_카테고리를_생성한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 상위_카테고리_생성_응답 = 카테고리_생성_요청(말랑_세션_ID, "Spring", 없음());
        var 상위_카테고리_ID = ID를_추출한다(상위_카테고리_생성_응답);

        // when
        var 응답 = 카테고리_생성_요청(말랑_세션_ID, "JPA", 상위_카테고리_ID);

        // then
        응답_상태를_검증한다(응답, 생성됨);
        값이_존재한다(ID를_추출한다(응답));
    }
}