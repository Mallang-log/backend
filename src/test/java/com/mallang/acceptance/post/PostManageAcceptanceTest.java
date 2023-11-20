package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.공개;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비공개;
import static com.mallang.acceptance.comment.CommentAcceptanceSteps.비인증_댓글_작성;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호되지_않음;
import static com.mallang.acceptance.post.PostAcceptanceSteps.좋아요_안눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_데이터;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_삭제_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_수정_요청;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.acceptance.AcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 관리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostManageAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 동훈_블로그_ID;
    private Long 카테고리_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_블로그_ID = 블로그_개설(동훈_세션_ID, "donghun-log");
        카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
    }

    @Test
    void 포스트를_작성한다() {
        // when
        var 응답 = 포스트_생성_요청(
                말랑_세션_ID,
                말랑_블로그_ID,
                "첫 포스트",
                "첫 포스트이네요.",
                "포스트 썸네일 이름",
                "첫 포스트 인트로",
                PUBLIC,
                없음(),
                카테고리_ID,
                "태그1", "태그2"
        );

        // then
        응답_상태를_검증한다(응답, 생성됨);
        var 포스트_ID = ID를_추출한다(응답);
        값이_존재한다(포스트_ID);
    }

    @Test
    void 포스트를_업데이트한다() {
        // given
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_ID));

        // when
        var 응답 = 포스트_수정_요청(
                말랑_세션_ID,
                포스트_ID,
                "업데이트 제목",
                "업데이트 내용",
                "업데이트 포스트 썸네일 이름",
                "업데이트 인트로",
                PRIVATE,
                없음(),
                카테고리_ID,
                "태그1", "태그2"
        );

        // then
        응답_상태를_검증한다(응답, 정상_처리);
        var 조회_결과 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID, null);
        var 예상_데이터 = 포스트_단일_조회_데이터(
                포스트_ID,
                "말랑",
                카테고리_ID,
                "Spring",
                "업데이트 제목",
                "업데이트 내용",
                "업데이트 포스트 썸네일 이름",
                PRIVATE,
                보호되지_않음,
                좋아요_안눌림,
                0,
                "태그1", "태그2");
        포스트_단일_조회_응답을_검증한다(조회_결과, 예상_데이터);
    }

    @Test
    void 포스트를_삭제한다() {
        // given
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_ID));
        var 다른_회원_세션_ID = 회원가입과_로그인_후_세션_ID_반환("다른회원");

        var 댓글1_ID = 비인증_댓글_작성(포스트_ID, "댓글", "비인증", "1234", null);
        var 댓글2_ID = 댓글_작성(다른_회원_세션_ID, 포스트_ID, "댓글", 비공개, null);
        비인증_댓글_작성(포스트_ID, "대댓글", "비인증", "1234", 댓글1_ID, null);
        댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글", 공개, 댓글1_ID, null);
        비인증_댓글_작성(포스트_ID, "대댓글", "비인증", "1234", 댓글2_ID, null);

        // when
        var 응답 = 포스트_삭제_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 본문_없음);
        응답_상태를_검증한다(포스트_단일_조회_요청(null, 포스트_ID, null), 찾을수_없음);
    }
}
