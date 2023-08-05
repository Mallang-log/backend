package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성_요청을_보낸다;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_게시글_단일_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_게시글_전체_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.전체_조회_항목들;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_단일_조회_요청을_보낸다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_생성_요청을_보낸다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_수정_요청을_보낸다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_전체_조회_요청을_보낸다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.게시글_전체_조회_응답을_검증한다;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("게시글 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class PostAcceptanceTest extends AcceptanceTest {

    @Test
    void 게시글을_작성한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 카테고리_ID = ID를_추출한다(카테고리_생성_요청을_보낸다(말랑_세션_ID, "Spring", 없음()));

        // when
        var 응답 = 게시글_생성_요청을_보낸다(말랑_세션_ID, "첫 게시글", "첫 게시글이네요.", 카테고리_ID);

        // then
        응답_상태를_검증한다(응답, 생성됨);
        var 생성된_게시글_ID = ID를_추출한다(응답);
        값이_존재한다(생성된_게시글_ID);
    }

    @Test
    void 게시글을_업데이트한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 생성된_게시글_ID = ID를_추출한다(게시글_생성_요청을_보낸다(말랑_세션_ID, "첫 게시글", "첫 게시글이네요.", 없음()));
        var 카테고리_ID = ID를_추출한다(카테고리_생성_요청을_보낸다(말랑_세션_ID, "Spring", 없음()));

        // when
        var 응답 = 게시글_수정_요청을_보낸다(말랑_세션_ID, 생성된_게시글_ID, "업데이트 제목", "업데이트 내용", 카테고리_ID);

        // then
        응답_상태를_검증한다(응답, 정상_처리);
        var 예상_데이터 = 예상_게시글_단일_조회_응답(생성된_게시글_ID, "말랑", 카테고리_ID, "Spring", "업데이트 제목", "업데이트 내용");
        var 조회_결과 = 게시글_단일_조회_요청을_보낸다(생성된_게시글_ID);
        게시글_단일_조회_응답을_검증한다(조회_결과, 예상_데이터);
    }

    @Test
    void 게시글_단일_조회() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 카테고리_ID = ID를_추출한다(카테고리_생성_요청을_보낸다(말랑_세션_ID, "Spring", 없음()));
        var 생성된_게시글_ID = ID를_추출한다(게시글_생성_요청을_보낸다(말랑_세션_ID, "첫 게시글", "첫 게시글이네요.", 카테고리_ID));
        var 예상_데이터 = 예상_게시글_단일_조회_응답(생성된_게시글_ID, "말랑", 카테고리_ID, "Spring", "첫 게시글", "첫 게시글이네요.");

        // when
        var 응답 = 게시글_단일_조회_요청을_보낸다(생성된_게시글_ID);

        // then
        게시글_단일_조회_응답을_검증한다(응답, 예상_데이터);
    }

    @Test
    void 없는_게시글_단일_조회() {
        // given
        var 없는_ID = 100L;

        // when
        var 응답 = 게시글_단일_조회_요청을_보낸다(없는_ID);

        // then
        응답_상태를_검증한다(응답, 찾을수_없음);
    }

    @Test
    void 게시글_전체_조회() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 카테고리_ID = ID를_추출한다(카테고리_생성_요청을_보낸다(말랑_세션_ID, "Spring", 없음()));
        var 게시글1_ID = ID를_추출한다(게시글_생성_요청을_보낸다(말랑_세션_ID, "게시글1", "이건 첫번째 게시글이네요.", 없음()));
        var 게시글2_ID = ID를_추출한다(게시글_생성_요청을_보낸다(말랑_세션_ID, "게시글2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID));
        var 게시글3_ID = ID를_추출한다(게시글_생성_요청을_보낸다(말랑_세션_ID, "게시글3", "잘 알아보았어요!", 카테고리_ID));
        var 예상_데이터 = 전체_조회_항목들(
                예상_게시글_전체_조회_응답(게시글1_ID, "말랑", 없음(), 없음(), "게시글1", "이건 첫번째 게시글이네요."),
                예상_게시글_전체_조회_응답(게시글2_ID, "말랑", 카테고리_ID, "Spring", "게시글2", "이번에는 이것 저것들에 대해 알아보아요"),
                예상_게시글_전체_조회_응답(게시글3_ID, "말랑", 카테고리_ID, "Spring", "게시글3", "잘 알아보았어요!")
        );

        // when
        var 응답 = 게시글_전체_조회_요청을_보낸다();

        // then
        게시글_전체_조회_응답을_검증한다(응답, 예상_데이터);
    }
}
