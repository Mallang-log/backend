package com.mallang.acceptance.comment;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_삭제_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_수정_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.익명_댓글_작성_요청;
import static com.mallang.acceptance.comment.CommentAcceptanceTestHelper.댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceTestHelper.익명_댓글_작성;
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

    // TODO 조회를 통한 검증
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
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", false);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_Id = ID를_추출한다(응답);
            값이_존재한다(댓글_Id);
        }

        @Test
        void 로그인한_사용자는_비밀_댓글을_작성할_수_있다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());

            // when
            var 응답 = 댓글_작성_요청(동훈_세션_ID, 포스트_ID, "댓글", true);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            var 댓글_Id = ID를_추출한다(응답);
            값이_존재한다(댓글_Id);
        }
    }

    @Nested
    class 댓글_수정_시 extends AcceptanceTest {

        @Test
        void 자신의_댓글을_수정한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", true);

            // when
            var 응답 = 댓글_수정_요청(동훈_세션_ID, 댓글_ID, "수정");

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_댓글이_아닌_경우_오류이다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 다른사람_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", true);

            // when
            var 응답 = 댓글_수정_요청(다른사람_세션_ID, 댓글_ID, "수정");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 익명_댓글을_수정한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 댓글_ID = 익명_댓글_작성(포스트_ID, "좋은 글 감사합니다", "익명입니다", "1234");

            // when
            var 응답 = 댓글_수정_요청(댓글_ID, "1234", "수정");

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 익명_댓글_수정_시_비밀번호가_다르면_오류() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 댓글_ID = 익명_댓글_작성(포스트_ID, "좋은 글 감사합니다", "익명입니다", "1234");

            // when
            var 응답 = 댓글_수정_요청(댓글_ID, "12345", "수정");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 포스트_작성자라도_댓글을_수정할_수는_없다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", true);

            // when
            var 응답 = 댓글_수정_요청(말랑_세션_ID, 댓글_ID, "수정");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 댓글_삭제_시 extends AcceptanceTest {

        @Test
        void 자신의_댓글을_삭제한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", true);

            // when
            var 응답 = 댓글_삭제_요청(동훈_세션_ID, 댓글_ID);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_댓글이_아닌_경우_오류이다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
            var 다른사람_세션_ID = 회원가입과_로그인_후_세션_ID_반환("other");
            var 댓글_ID = 댓글_작성(동훈_세션_ID, 포스트_ID, "좋은 글 감사합니다", true);

            // when
            var 응답 = 댓글_삭제_요청(다른사람_세션_ID, 댓글_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 익명_댓글을_삭제한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 댓글_ID = 익명_댓글_작성(포스트_ID, "좋은 글 감사합니다", "익명입니다", "1234");

            // when
            var 응답 = 댓글_삭제_요청(댓글_ID, "1234");

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 익명_댓글_삭제_시_비밀번호가_다르면_오류() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 댓글_ID = 익명_댓글_작성(포스트_ID, "좋은 글 감사합니다", "익명입니다", "1234");

            // when
            var 응답 = 댓글_삭제_요청(댓글_ID, "123");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 포스트_작성자는_댓글을_삭제가_가능하다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, "제목", "내용", 없음());
            var 댓글_ID = 익명_댓글_작성(포스트_ID, "좋은 글 감사합니다", "익명입니다", "1234");

            // when
            var 응답 = 댓글_삭제_요청(말랑_세션_ID, 댓글_ID);

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }
    }
}
