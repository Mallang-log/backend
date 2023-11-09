package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceTestHelper.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceTestHelper.카테고리_생성;
import static com.mallang.acceptance.comment.CommentAcceptanceDatas.공개;
import static com.mallang.acceptance.comment.CommentAcceptanceDatas.비공개;
import static com.mallang.acceptance.comment.CommentAcceptanceTestHelper.댓글_작성;
import static com.mallang.acceptance.comment.CommentAcceptanceTestHelper.비인증_댓글_작성;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_포스트_단일_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.예상_포스트_전체_조회_응답;
import static com.mallang.acceptance.post.PostAcceptanceDatas.전체_조회_항목들;
import static com.mallang.acceptance.post.PostAcceptanceDatas.좋아요_눌림;
import static com.mallang.acceptance.post.PostAcceptanceDatas.좋아요_안눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_삭제_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_생성_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_수정_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceTestHelper.포스트_생성;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.포스트_좋아요_요청;
import static com.mallang.post.domain.visibility.PostVisibility.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibility.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibility.Visibility.PUBLIC;

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
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());

        // when
        var 응답 = 포스트_생성_요청(말랑_세션_ID, 블로그_ID,
                "첫 포스트", "첫 포스트이네요.",
                PUBLIC, 없음(),
                카테고리_ID, "태그1", "태그2");

        // then
        응답_상태를_검증한다(응답, 생성됨);
        var 생성된_포스트_ID = ID를_추출한다(응답);
        값이_존재한다(생성된_포스트_ID);
    }

    @Test
    void 포스트를_업데이트한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 생성된_포스트_ID = 포스트_생성(말랑_세션_ID,
                블로그_ID, "첫 포스트", "첫 포스트이네요.",
                PROTECTED, "1234",
                없음(), "태그1");
        var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());

        // when
        var 응답 = 포스트_수정_요청(말랑_세션_ID, 생성된_포스트_ID,
                "업데이트 제목", "업데이트 내용",
                PRIVATE, 없음(),
                카테고리_ID, "태그1", "태그2");

        // then
        응답_상태를_검증한다(응답, 정상_처리);
        var 조회_결과 = 포스트_단일_조회_요청(말랑_세션_ID, 생성된_포스트_ID);
        var 예상_데이터 = 예상_포스트_단일_조회_응답(생성된_포스트_ID, "말랑",
                카테고리_ID, "Spring",
                "업데이트 제목", "업데이트 내용",
                PRIVATE, 좋아요_안눌림,
                "태그1", "태그2");
        포스트_단일_조회_응답을_검증한다(조회_결과, 예상_데이터);
    }

    @Test
    void 포스트를_삭제한다() {
        // given
        var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음());

        var 다른_회원_세션_ID = 회원가입과_로그인_후_세션_ID_반환("다른회원");

        var 댓글1_ID = 비인증_댓글_작성(포스트_ID, "댓글", "비인증", "1234");
        var 댓글2_ID = 댓글_작성(다른_회원_세션_ID, 포스트_ID, "댓글", 비공개);
        비인증_댓글_작성(포스트_ID, "대댓글", "비인증", "1234", 댓글1_ID);
        댓글_작성(말랑_세션_ID, 포스트_ID, "대댓글", 공개, 댓글1_ID);
        비인증_댓글_작성(포스트_ID, "대댓글", "비인증", "1234", 댓글2_ID);

        // when
        var 응답 = 포스트_삭제_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 본문_없음);
        응답_상태를_검증한다(포스트_단일_조회_요청(포스트_ID), 찾을수_없음);
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 포스트를_단일_조회한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());
            var 생성된_포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 카테고리_ID, "태그1");

            // when
            var 응답 = 포스트_단일_조회_요청(생성된_포스트_ID);

            // then
            var 예상_데이터 = 예상_포스트_단일_조회_응답(생성된_포스트_ID, "말랑",
                    카테고리_ID, "Spring",
                    "첫 포스트", "첫 포스트이네요.",
                    PUBLIC, 좋아요_안눌림,
                    "태그1");
            포스트_단일_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 없는_포스트를_단일_조회한다면_예외() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 없는_ID = 100L;

            // when
            var 응답 = 포스트_단일_조회_요청(없는_ID);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }

        @Test
        void 좋아요_눌렀는지_여부가_반영된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "첫 포스트", "첫 포스트이네요.", 없음());
            포스트_좋아요_요청(말랑_세션_ID, 포스트_ID);

            // when
            var 좋아요_눌린_응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID);
            var 좋아요_안눌린_응답 = 포스트_단일_조회_요청(포스트_ID);

            // then
            포스트_단일_조회_응답을_검증한다(좋아요_눌린_응답,
                    예상_포스트_단일_조회_응답(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.",
                            PUBLIC, 좋아요_눌림, 1));
            포스트_단일_조회_응답을_검증한다(좋아요_안눌린_응답,
                    예상_포스트_단일_조회_응답(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.",
                            PUBLIC, 좋아요_안눌림, 1));
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                    "첫 포스트", "첫 포스트이네요.",
                    PRIVATE, 없음(), 없음());

            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    예상_포스트_단일_조회_응답(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.",
                            PRIVATE, 좋아요_안눌림));
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                    "첫 포스트", "첫 포스트이네요.",
                    PRIVATE, 없음(), 없음());

            // when
            var 응답 = 포스트_단일_조회_요청(포스트_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 블로그_주인은_보호글을_볼_수_있다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                    "첫 포스트", "첫 포스트이네요.",
                    PROTECTED, "1234", 없음());

            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    예상_포스트_단일_조회_응답(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.",
                            PROTECTED, 좋아요_안눌림));
        }

        @Test
        void 블로그_주인이_아닌_경우_보호글_조회시_내용이_보호된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 블로그_ID,
                    "첫 포스트", "첫 포스트이네요.",
                    PROTECTED, "1234", 없음());

            // when
            var 응답 = 포스트_단일_조회_요청(포스트_ID);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    예상_포스트_단일_조회_응답(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                            PROTECTED, 좋아요_안눌림));
        }
    }

    @Nested
    class 포스트_검색_시 {

        @Test
        void 포스트를_전체_조회한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID, "태그1");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID, "태그1", "태그2");

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 없음(), 없음(), 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "이건 첫번째 포스트이네요.",
                            PUBLIC),
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요",
                            PUBLIC, "태그1"),
                    예상_포스트_전체_조회_응답(포스트3_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트3", "잘 알아보았어요!",
                            PUBLIC, "태그1", "태그2")
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 특정_카테고리로_검색하면_해당_카테고리와_하위_카테고리에_해당하는_포스트가_조회된다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID);

            // when
            var 응답 = 포스트_전체_조회_요청(카테고리_ID, 블로그_ID, 없음(), 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요",
                            PUBLIC),
                    예상_포스트_전체_조회_응답(포스트3_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트3", "잘 알아보았어요!",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 태그로_필터링() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 카테고리_ID = 카테고리_생성(말랑_세션_ID, 블로그_ID, "Spring", 없음());
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트1", "이건 첫번째 포스트이네요.", 없음(), "tag1", "tag2", "tag3");
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 카테고리_ID, "tag1", "tag2", "tag4");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트3", "잘 알아보았어요!", 카테고리_ID, "tag2", "tag3");

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 블로그_ID, "tag1", 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "이건 첫번째 포스트이네요.",
                            PUBLIC, "tag1", "tag2", "tag3"),
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요",
                            PUBLIC, "tag1", "tag2", "tag4")
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 제목으로_검색한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트1", "포스트1입니다.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스 트2", "포스트2입니다.", 없음());

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 블로그_ID, 없음(), 없음(), "포스트", 없음(), 없음());

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "포스트1입니다.",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 내용으로_검색한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트 1", "포스트 1입니다.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트 2", "포스트 2입니다.", 없음());

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 블로그_ID, 없음(), 없음(), 없음(), "포스트 2", 없음());

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑",
                            없음(), 없음(),
                            "포스트 2", "포스트 2입니다.",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 제목_내용으로_검색한다() {
            // given
            var 말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
            var 블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "포스트 1", "포스트 1입니다.", 없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 블로그_ID, "2번째", "포스트 2입니다.", 없음());

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 블로그_ID, 없음(), 없음(), 없음(), 없음(), "포스트");

            // then
            var 예상_데이터 = 전체_조회_항목들(
                    예상_포스트_전체_조회_응답(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트 1", "포스트 1입니다.",
                            PUBLIC),
                    예상_포스트_전체_조회_응답(포스트2_ID, "말랑",
                            없음(), 없음(),
                            "2번째", "포스트 2입니다.",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }
    }
}
