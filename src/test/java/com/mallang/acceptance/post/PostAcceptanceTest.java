package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호되지_않음;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호됨;
import static com.mallang.acceptance.post.PostAcceptanceSteps.좋아요_눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.좋아요_안눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_데이터;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_데이터;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.포스트_좋아요_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;

import com.mallang.acceptance.AcceptanceTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private String 말랑_블로그_이름;
    private String 동훈_블로그_이름;
    private Long 카테고리_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_블로그_이름 = 블로그_개설(동훈_세션_ID, "donghun-log");
        카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_이름, "Spring", 없음());
    }

    @Nested
    class 포스트_단일_조회_시 extends AcceptanceTest {

        @Test
        void 포스트를_단일_조회한다() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "썸네일",
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    카테고리_ID,
                    "태그1"
            );

            // when
            var 응답 = 포스트_단일_조회_요청(null, 포스트_ID, null);

            // then
            var 예상_데이터 = 포스트_단일_조회_데이터(
                    포스트_ID,
                    "말랑",
                    카테고리_ID,
                    "Spring",
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "썸네일",
                    PUBLIC,
                    보호되지_않음,
                    좋아요_안눌림,
                    0,
                    "태그1"
            );
            포스트_단일_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 없는_포스트를_단일_조회한다면_예외() {
            // given
            var 없는_ID = 100L;

            // when
            var 응답 = 포스트_단일_조회_요청(null, 없는_ID, null);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }

        @Test
        void 좋아요_눌렀는지_여부가_반영된다() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "포스트 썸네일",
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음()
            );
            포스트_좋아요_요청(말랑_세션_ID, 포스트_ID, null);

            // when
            var 좋아요_눌린_응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID, null);
            var 좋아요_안눌린_응답 = 포스트_단일_조회_요청(null, 포스트_ID, null);

            // then
            포스트_단일_조회_응답을_검증한다(좋아요_눌린_응답,
                    포스트_단일_조회_데이터(포스트_ID,
                            "말랑",
                            없음(),
                            없음(),
                            "첫 포스트",
                            "첫 포스트이네요.",
                            "포스트 썸네일",
                            PUBLIC,
                            보호되지_않음,
                            좋아요_눌림,
                            1));
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "포스트 썸네일",
                    "첫 포스트 인트로",
                    PRIVATE,
                    없음(),
                    없음()
            );

            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID, null);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    포스트_단일_조회_데이터(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.", "포스트 썸네일",
                            PRIVATE, 보호되지_않음, 좋아요_안눌림, 0));
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "첫 포스트 썸네일",
                    "첫 포스트 인트로",
                    PRIVATE,
                    없음(),
                    없음()
            );

            // when
            var 응답 = 포스트_단일_조회_요청(null, 포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 블로그_주인은_보호글을_볼_수_있다() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "이미지이름",
                    "첫 포스트 인트로",
                    PROTECTED,
                    "1234",
                    없음()
            );

            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID, null);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    포스트_단일_조회_데이터(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트", "첫 포스트이네요.", "이미지이름",
                            PROTECTED, 보호되지_않음, 좋아요_안눌림, 0));
        }

        @Test
        void 블로그_주인이_아닌_경우_보호글_조회시_내용과_썸네일_이미지가_보호된다() {
            // given
            var 포스트_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "첫 포스트",
                    "첫 포스트이네요.",
                    "첫 포스트 인트로",
                    "이미지이름",
                    PROTECTED,
                    "1234",
                    없음()
            );

            // when
            var 응답 = 포스트_단일_조회_요청(null, 포스트_ID, null);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    포스트_단일_조회_데이터(포스트_ID, "말랑",
                            없음(), 없음(),
                            "첫 포스트",
                            "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                            "",
                            PROTECTED, 보호됨, 좋아요_안눌림, 0));
        }
    }

    @Nested
    class 포스트_검색_시 extends AcceptanceTest {

        @Test
        void 포스트를_전체_조회한다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트1", "이건 첫번째 포스트이네요.", "썸넬1", "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트2", "이번에는 이것 저것들에 대해 알아보아요", "썸넬2", "2 포스트 인트로",
                    PUBLIC,
                    없음(),
                    카테고리_ID, "태그1");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트3", "잘 알아보았어요!", null, "3 포스트 인트로",
                    PUBLIC,
                    없음(),
                    카테고리_ID, "태그1", "태그2");

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 없음(), 없음(), 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트3_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트3", "잘 알아보았어요!", null, "3 포스트 인트로",
                            PUBLIC, "태그1", "태그2"),
                    포스트_전체_조회_데이터(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요", "썸넬2", "2 포스트 인트로",
                            PUBLIC, "태그1"),
                    포스트_전체_조회_데이터(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "이건 첫번째 포스트이네요.", "썸넬1", "첫 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 비공개_포스트인_경우_주인에게만_조회되며_나머지_포스트는_모든_사람이_조회할_수_있다() {
            // given
            Long 말랑_공개_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "mallang-public", "mallang-public-content",
                    null, "mallang 첫 포스트 인트로",
                    PUBLIC, 없음(), 없음());
            Long 말랑_보호_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "mallang-protected", "mallang-protected-content",
                    null, "mallang 2 포스트 인트로",
                    PROTECTED, "1234", 없음());
            Long 말랑_비공개_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "mallang-private", "mallang-private-content",
                    null, "mallang 3 포스트 인트로",
                    PRIVATE, 없음(), 없음());

            Long 동훈_공개_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_블로그_이름,
                    "donghun-public", "donghun-public-content",
                    null, "donghun 첫 포스트 인트로",
                    PUBLIC, 없음(), 없음());
            Long 동훈_보호_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_블로그_이름,
                    "donghun-protected", "donghun-protected-content",
                    null, "donghun 2 포스트 인트로",
                    PROTECTED, "123455", 없음());
            Long 동훈_비공개_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_블로그_이름,
                    "donghun-private", "donghun-private-content",
                    null, "donghun 3 포스트 인트로",
                    PRIVATE, 없음(), 없음());

            // when
            var 응답 = 포스트_전체_조회_요청(동훈_세션_ID, 없음(), 없음(), 없음(), 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(

                    포스트_전체_조회_데이터(동훈_비공개_포스트_ID, "동훈",
                            없음(), 없음(),
                            "donghun-private", "donghun-private-content",
                            null, "donghun 3 포스트 인트로",
                            PRIVATE),

                    포스트_전체_조회_데이터(동훈_보호_포스트_ID, "동훈",
                            없음(), 없음(),
                            "donghun-protected", "donghun-protected-content",
                            null, "donghun 2 포스트 인트로",
                            PROTECTED),

                    포스트_전체_조회_데이터(동훈_공개_포스트_ID, "동훈",
                            없음(), 없음(),
                            "donghun-public", "donghun-public-content",
                            null, "donghun 첫 포스트 인트로",
                            PUBLIC),

                    포스트_전체_조회_데이터(말랑_보호_포스트_ID, "말랑",
                            없음(), 없음(),
                            "mallang-protected",
                            "보호되어 있는 글입니다.",
                            "",
                            "",
                            PROTECTED),

                    포스트_전체_조회_데이터(
                            말랑_공개_포스트_ID,
                            "말랑",
                            없음(), 없음(),
                            "mallang-public",
                            "mallang-public-content",
                            null,
                            "mallang 첫 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 특정_카테고리로_검색하면_해당_카테고리와_하위_카테고리에_해당하는_포스트가_조회된다() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트1", "이건 첫번째 포스트이네요.", 없음(), "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음());
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 없음(), "2 포스트 인트로",
                    PUBLIC,
                    없음(),
                    카테고리_ID);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트3", "잘 알아보았어요!", 없음(), "3 포스트 인트로",
                    PUBLIC,
                    없음(),
                    카테고리_ID);

            // when
            var 응답 = 포스트_전체_조회_요청(카테고리_ID, 말랑_블로그_이름, 없음(), 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트3_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트3", "잘 알아보았어요!",
                            null, "3 포스트 인트로",
                            PUBLIC),
                    포스트_전체_조회_데이터(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요",
                            null, "2 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 태그로_필터링() {
            // given
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트1", "이건 첫번째 포스트이네요.", 없음(), "첫 포스트 인트로",
                    PUBLIC, 없음(), 없음(),
                    "tag1", "tag2", "tag3");
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트2", "이번에는 이것 저것들에 대해 알아보아요", 없음(), "2 포스트 인트로",
                    PUBLIC, 없음(),
                    카테고리_ID, "tag1", "tag2", "tag4");
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 말랑_블로그_이름,
                    "포스트3", "잘 알아보았어요!", 없음(), "3 포스트 인트로",
                    PUBLIC, 없음(),
                    카테고리_ID, "tag2", "tag3");

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 말랑_블로그_이름, "tag1", 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트2_ID, "말랑",
                            카테고리_ID, "Spring",
                            "포스트2", "이번에는 이것 저것들에 대해 알아보아요",
                            null, "2 포스트 인트로",
                            PUBLIC, "tag1", "tag2", "tag4"),
                    포스트_전체_조회_데이터(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "이건 첫번째 포스트이네요.",
                            null, "첫 포스트 인트로",
                            PUBLIC, "tag1", "tag2", "tag3")
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 제목으로_검색한다() {
            // given
            var 포스트1_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "포스트1",
                    "포스트1입니다.",
                    없음(),
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음()
            );
            var 포스트2_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "포스 트2",
                    "포스트2입니다.",
                    없음(),
                    "2 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음()
            );

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 말랑_블로그_이름, 없음(), 없음(), "포스트", 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트1", "포스트1입니다.",
                            null, "첫 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 내용으로_검색한다() {
            // given
            var 포스트1_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "포스트 1",
                    "포스트 1입니다.",
                    없음(),
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음());
            var 포스트2_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "포스트 2",
                    "포스트 2입니다.",
                    없음(),
                    "2 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음());

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 말랑_블로그_이름, 없음(), 없음(), 없음(), "포스트 2", 없음());

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트2_ID, "말랑",
                            없음(), 없음(),
                            "포스트 2", "포스트 2입니다.",
                            null, "2 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 제목_내용으로_검색한다() {
            // given
            var 포스트1_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "포스트 1",
                    "포스트 1입니다.",
                    없음(),
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음()
            );
            var 포스트2_ID = 포스트_생성(
                    말랑_세션_ID,
                    말랑_블로그_이름,
                    "2번째",
                    "포스트 2입니다.",
                    없음(),
                    "2 포스트 인트로",
                    PUBLIC,
                    없음(),
                    없음()
            );

            // when
            var 응답 = 포스트_전체_조회_요청(없음(), 말랑_블로그_이름, 없음(), 없음(), 없음(), 없음(), "포스트");

            // then
            var 예상_데이터 = List.of(
                    포스트_전체_조회_데이터(포스트2_ID, "말랑",
                            없음(), 없음(),
                            "2번째", "포스트 2입니다.",
                            null, "2 포스트 인트로",
                            PUBLIC),
                    포스트_전체_조회_데이터(포스트1_ID, "말랑",
                            없음(), 없음(),
                            "포스트 1", "포스트 1입니다.",
                            null, "첫 포스트 인트로",
                            PUBLIC)
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }
    }
}
