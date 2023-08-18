package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.category.CategoryAcceptanceTestHelper.카테고리_생성;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_포스트_단일_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_포스트_전체_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.전체_조회_항목들;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_생성_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_수정_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceTestHelper.포스트_생성;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class PostAcceptanceTest extends AcceptanceTest {

    @Test
    void 포스트를_작성한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());

        // when
        var 응답 = 포스트_생성_요청(말랑_세션_ID, "첫 포스트", "첫 포스트이네요.", 카테고리_ID, "태그1", "태그2");

        // then
        응답_상태를_검증한다(응답, 생성됨);
        var 생성된_포스트_ID = ID를_추출한다(응답);
        값이_존재한다(생성된_포스트_ID);
    }

    @Test
    void 포스트를_업데이트한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 생성된_포스트_ID = 포스트_생성(말랑_세션_ID, "첫 포스트", "첫 포스트이네요.", 없음());
        var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());

        // when
        var 응답 = 포스트_수정_요청(말랑_세션_ID, 생성된_포스트_ID, "업데이트 제목", "업데이트 내용", 카테고리_ID);

        // then
        응답_상태를_검증한다(응답, 정상_처리);
        var 예상_데이터 = 예상_포스트_단일_조회_응답(생성된_포스트_ID, "말랑", 카테고리_ID, "Spring", "업데이트 제목", "업데이트 내용");
        var 조회_결과 = 포스트_단일_조회_요청(생성된_포스트_ID);
        포스트_단일_조회_응답을_검증한다(조회_결과, 예상_데이터);
    }

    @Test
    void 포스트_단일_조회() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());
        var 생성된_포스트_ID = 포스트_생성(말랑_세션_ID, "첫 포스트", "첫 포스트이네요.", 카테고리_ID, "태그1");
        var 예상_데이터 = 예상_포스트_단일_조회_응답(생성된_포스트_ID, "말랑", 카테고리_ID, "Spring", "첫 포스트", "첫 포스트이네요.", "태그1");

        // when
        var 응답 = 포스트_단일_조회_요청(생성된_포스트_ID);

        // then
        포스트_단일_조회_응답을_검증한다(응답, 예상_데이터);
    }

    @Test
    void 없는_포스트_단일_조회() {
        // given
        var 없는_ID = 100L;

        // when
        var 응답 = 포스트_단일_조회_요청(없는_ID);

        // then
        응답_상태를_검증한다(응답, 찾을수_없음);
    }

    @Nested
    class 포스트_검색_시 extends AcceptanceTest {

        @Test
        void 포스트_전체_조회() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID, "태그1");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID, "태그1", "태그2");
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트1_ID, "말랑", 없음(), 없음(), "포스트1", "이건 첫번째 포스트이네요."),
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑", 카테고리_ID, "Spring", "포스트2", "이번에는 이것 저것들에 대해 알아보아요", "태그1"),
                    예상_포스트_전체_조회_응답(포스트3_ID, "말랑", 카테고리_ID, "Spring", "포스트3", "잘 알아보았어요!", "태그1", "태그2")
            );

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 없음());

            // then
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 특정_카테고리로_검색하면_해당_카테고리와_하위_카테고리에_해당하는_포스트가_조회된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID);
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑", 카테고리_ID, "Spring", "포스트2", "이번에는 이것 저것들에 대해 알아보아요"),
                    예상_포스트_전체_조회_응답(포스트3_ID, "말랑", 카테고리_ID, "Spring", "포스트3", "잘 알아보았어요!")
            );

            // when
            var 응답 = 포스트_전체_조회_요청(카테고리_ID, 없음());

            // then
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 태그로_필터링() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음(), "tag1");
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID, "tag1");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID, "tag2");
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑", 카테고리_ID, "Spring", "포스트2", "이번에는 이것 저것들에 대해 알아보아요", "tag1")
            );

            // when
            var 응답 = 포스트_전체_조회_요청(카테고리_ID, "tag1");

            // then
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }
    }
}
