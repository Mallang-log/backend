package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호되지_않음;
import static com.mallang.acceptance.post.PostAcceptanceSteps.좋아요_안눌림;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.공개_포스트_생성_데이터;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.내_관리_글_단일_조회_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.내_관리_글_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.내_관리_글_목록_조회_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.내_관리_글_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_삭제_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_수정_요청;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.presentation.request.UpdatePostRequest;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostDetailResponse.WriterResponse;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageDetailResponse.CategoryResponse;
import com.mallang.post.query.response.PostManageDetailResponse.TagResponses;
import com.mallang.post.query.response.PostManageSearchResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 관리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostManageAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private Long 말랑_블로그_ID;
    private Long 동훈_블로그_ID;
    private Long Spring_카테고리_ID;
    private Long JPA_카테고리_ID;
    private Long Front_카테고리_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_ID = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_블로그_ID = 블로그_개설(동훈_세션_ID, "donghun-log");
        Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Spring", 없음());
        JPA_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "JPA", Spring_카테고리_ID);
        Front_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_ID, "Front", 없음());
    }

    @Test
    void 포스트를_작성한다() {
        // when
        CreatePostRequest 요청 = new CreatePostRequest(말랑_블로그_ID,
                "첫 포스트",
                "첫 포스트이네요.",
                "포스트 썸네일 이름",
                "첫 포스트 인트로",
                PUBLIC,
                없음(),
                Spring_카테고리_ID,
                List.of("태그1", "태그2")
        );
        var 응답 = 포스트_생성_요청(말랑_세션_ID, 요청);

        // then
        응답_상태를_검증한다(응답, 생성됨);
    }

    @Test
    void 포스트를_업데이트한다() {
        // given
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_ID));

        // when
        UpdatePostRequest 포스트_업데이트_요청 = new UpdatePostRequest("업데이트 제목",
                "업데이트 내용",
                "업데이트 포스트 썸네일 이름",
                "업데이트 인트로",
                PRIVATE,
                없음(),
                Spring_카테고리_ID,
                List.of("태그1", "태그2")
        );
        var 응답 = 포스트_수정_요청(말랑_세션_ID, 포스트_ID, 포스트_업데이트_요청);

        // then
        응답_상태를_검증한다(응답, 정상_처리);
        var 조회_결과 = 포스트_단일_조회_요청(말랑_세션_ID, 포스트_ID, null);
        var 예상_데이터 = new PostDetailResponse(
                포스트_ID,
                "업데이트 제목",
                "업데이트 내용",
                "업데이트 포스트 썸네일 이름",
                PRIVATE,
                보호되지_않음,
                null,
                0,
                좋아요_안눌림,
                null,
                new WriterResponse(null, "말랑", "말랑"),
                new PostDetailResponse.CategoryResponse(Spring_카테고리_ID, "Spring"),
                new PostDetailResponse.TagResponses(List.of("태그1", "태그2"))
        );
        포스트_단일_조회_응답을_검증한다(조회_결과, 예상_데이터);
    }

    @Test
    void 포스트를_삭제한다() {
        // given
        var 포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_생성_데이터(말랑_블로그_ID));
        var 다른_회원_세션_ID = 회원가입과_로그인_후_세션_ID_반환("다른회원");

        // when
        var 응답 = 포스트_삭제_요청(말랑_세션_ID, 포스트_ID);

        // then
        응답_상태를_검증한다(응답, 본문_없음);
        응답_상태를_검증한다(포스트_단일_조회_요청(null, 포스트_ID, null), 찾을수_없음);
    }

    @Nested
    class 내_글_관리_목록_조회_API {

        private Long public_spring_포스트_ID;
        private Long protected_jpa_포스트_ID;
        private Long private_front_포스트_ID;

        @BeforeEach
        void setUp() {
            CreatePostRequest public_spring_포스트_요청 = new CreatePostRequest(
                    말랑_블로그_ID,
                    "Spring 입니다",
                    "첫 포스트이네요.",
                    "포스트 썸네일 이름",
                    "첫 포스트 인트로",
                    PUBLIC,
                    null,
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2")
            );
            public_spring_포스트_ID = 포스트_생성(말랑_세션_ID, public_spring_포스트_요청);

            CreatePostRequest protected_jpa_포스트_요청 = new CreatePostRequest(말랑_블로그_ID,
                    "Jpa 입니다",
                    "이번에는 이것 저것들에 대해 알아보아요",
                    "썸넬2",
                    "2 포스트 인트로",
                    PROTECTED,
                    "1234",
                    JPA_카테고리_ID,
                    emptyList()
            );
            protected_jpa_포스트_ID = 포스트_생성(말랑_세션_ID, protected_jpa_포스트_요청);

            CreatePostRequest private_front_포스트_요청 = new CreatePostRequest(말랑_블로그_ID,
                    "Front 입니다",
                    "잘 알아보았어요!",
                    null,
                    "3 포스트 인트로",
                    PRIVATE,
                    null,
                    Front_카테고리_ID,
                    List.of("태그1")
            );
            private_front_포스트_ID = 포스트_생성(말랑_세션_ID, private_front_포스트_요청);
        }

        @Test
        void 내_포스트를_전체_조회한다() {
            // when
            var 응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, 없음(), 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    new PostManageSearchResponse(
                            private_front_포스트_ID,
                            "Front 입니다",
                            PRIVATE,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(Front_카테고리_ID, "Front")
                    ),
                    new PostManageSearchResponse(
                            protected_jpa_포스트_ID,
                            "Jpa 입니다",
                            PROTECTED,
                            "1234",
                            null,
                            new PostManageSearchResponse.CategoryResponse(JPA_카테고리_ID, "JPA")
                    ),
                    new PostManageSearchResponse(
                            public_spring_포스트_ID,
                            "Spring 입니다",
                            PUBLIC,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(Spring_카테고리_ID, "Spring")
                    )
            );
            내_관리_글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 내_블로그가_아닌_경우_빈_리스트_조회() {

            // when
            var 응답1 = 내_관리_글_목록_조회_요청(동훈_세션_ID, 말랑_블로그_ID, 없음(), 없음(), 없음(), 없음());
            var 응답2 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 동훈_블로그_ID, 없음(), 없음(), 없음(), 없음());

            // then
            내_관리_글_전체_조회_응답을_검증한다(응답1, emptyList());
            내_관리_글_전체_조회_응답을_검증한다(응답2, emptyList());
        }

        @Test
        void 카테고리로_조회_시_하위_카테고리도_모두_포함하여_조회한다() {
            // when
            var Spring_조회_응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, Spring_카테고리_ID, 없음(), 없음(), 없음());
            var JPA_조회_응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, JPA_카테고리_ID, 없음(), 없음(), 없음());

            // then
            내_관리_글_전체_조회_응답을_검증한다(Spring_조회_응답, List.of(
                    new PostManageSearchResponse(
                            protected_jpa_포스트_ID,
                            "Jpa 입니다",
                            PROTECTED,
                            "1234",
                            null,
                            new PostManageSearchResponse.CategoryResponse(JPA_카테고리_ID, "JPA")
                    ),
                    new PostManageSearchResponse(
                            public_spring_포스트_ID,
                            "Spring 입니다",
                            PUBLIC,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(Spring_카테고리_ID, "Spring")
                    )
            ));
            내_관리_글_전체_조회_응답을_검증한다(JPA_조회_응답, List.of(
                    new PostManageSearchResponse(
                            protected_jpa_포스트_ID,
                            "Jpa 입니다",
                            PROTECTED,
                            "1234",
                            null,
                            new PostManageSearchResponse.CategoryResponse(JPA_카테고리_ID, "JPA")
                    )
            ));
        }

        @Test
        void 카테고리_필터링_시_카테고리_없음으로도_조회할_수_있다() {
            // given
            var 카테고리_없음_조건 = -1L;
            CreatePostRequest 카테고리_없는_포스트_생성_요청 = new CreatePostRequest(말랑_블로그_ID,
                    "카테고리 없는거입니다.",
                    "포스트이네요.",
                    null,
                    "첫 포스트 인트로",
                    PUBLIC,
                    없음(),
                    null,
                    List.of("태그1", "태그2")
            );
            var 카테고리_없음_포스트_ID = 포스트_생성(말랑_세션_ID, 카테고리_없는_포스트_생성_요청);

            // when
            var 응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, 카테고리_없음_조건, 없음(), 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    new PostManageSearchResponse(
                            카테고리_없음_포스트_ID,
                            "카테고리 없는거입니다.",
                            PUBLIC,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(null, null)
                    )
            );
            내_관리_글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 제목으로_검색할_수_있다() {
            // when
            var 응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, 없음(), "PA", 없음(), 없음());

            // then
            var 예상_데이터 = List.of(
                    new PostManageSearchResponse(
                            protected_jpa_포스트_ID,
                            "Jpa 입니다",
                            PROTECTED,
                            "1234",
                            null,
                            new PostManageSearchResponse.CategoryResponse(JPA_카테고리_ID, "JPA")
                    )
            );
            내_관리_글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }


        @Test
        void 내용으로_검색할_수_있다() {
            // when
            var 응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, 없음(), 없음(), "알아보", 없음());

            // then
            var 예상_데이터 = List.of(
                    new PostManageSearchResponse(
                            private_front_포스트_ID,
                            "Front 입니다",
                            PRIVATE,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(Front_카테고리_ID, "Front")
                    ),
                    new PostManageSearchResponse(
                            protected_jpa_포스트_ID,
                            "Jpa 입니다",
                            PROTECTED,
                            "1234",
                            null,
                            new PostManageSearchResponse.CategoryResponse(JPA_카테고리_ID, "JPA")
                    )
            );
            내_관리_글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 공개범위로_검색할_수_있다() {
            // when
            var 응답 = 내_관리_글_목록_조회_요청(말랑_세션_ID, 말랑_블로그_ID, 없음(), 없음(), 없음(), PRIVATE);

            // then
            var 예상_데이터 = List.of(
                    new PostManageSearchResponse(
                            private_front_포스트_ID,
                            "Front 입니다",
                            PRIVATE,
                            null,
                            null,
                            new PostManageSearchResponse.CategoryResponse(Front_카테고리_ID, "Front")
                    )
            );
            내_관리_글_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }
    }

    @Nested
    class 내_관리_글_단일_조회_API {

        private Long 포스트_ID;

        @BeforeEach
        void setUp() {
            CreatePostRequest public_spring_포스트_요청 = new CreatePostRequest(
                    말랑_블로그_ID,
                    "포스트1",
                    "이건 첫번째 포스트이네요.",
                    "썸넬1",
                    "첫 포스트 인트로",
                    PROTECTED,
                    "12345",
                    Spring_카테고리_ID,
                    List.of("태그1", "태그2", "태그3", "태그4")
            );
            포스트_ID = 포스트_생성(말랑_세션_ID, public_spring_포스트_요청);
        }

        @Test
        void 나의_글을_관리용으로_단일_조회한다() {
            // when
            var 응답 = 내_관리_글_단일_조회_요청(말랑_세션_ID, 포스트_ID);

            // then
            내_관리_글_단일_조회_응답을_검증한다(응답,
                    new PostManageDetailResponse(포스트_ID,
                            "포스트1",
                            "첫 포스트 인트로",
                            "이건 첫번째 포스트이네요.",
                            "썸넬1",
                            PROTECTED,
                            "12345",
                            null,
                            new CategoryResponse(Spring_카테고리_ID, "Spring"),
                            new TagResponses(List.of("태그1", "태그2", "태그3", "태그4")))
            );
        }

        @Test
        void 냐의_글이_아닌_경우_예외() {
            // when
            var 응답 = 내_관리_글_단일_조회_요청(동훈_세션_ID, 포스트_ID);

            // then
            응답_상태를_검증한다(응답, 찾을수_없음);
        }
    }
}
