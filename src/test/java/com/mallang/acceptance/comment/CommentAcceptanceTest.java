package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.익명_댓글_작성_요청;
import static com.mallang.acceptance.post.PostAcceptanceTestHelper.포스트_생성;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class CommentAcceptanceTest extends AcceptanceTest {

    @Nested
    class 포스트에_댓글_작성_시 extends AcceptanceTest {

        @Test
        void 익명으로_댓글을_작성한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());

            // when
            var 응답 = 익명_댓글_작성_요청(포스트_ID, "댓글", "익명", "1234");

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_ID = ID를_추출한다(응답);
            값이_존재한다(댓글_ID);
        }

        @Test
        void 로그인한_사용자가_댓글을_작성한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글");

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_Id = ID를_추출한다(응답);
            값이_존재한다(댓글_Id);
        }
    }
}
