package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.ID를_추출한다;
import static com.mallang.acceptance.AcceptanceSteps.값이_존재한다;
import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.본문_없음;
import static com.mallang.acceptance.AcceptanceSteps.생성됨;
import static com.mallang.acceptance.AcceptanceSteps.없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.잘못된_요청;
import static com.mallang.acceptance.AcceptanceSteps.정상_처리;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.블로그의_카테고리_조회_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_계층구조_수정_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_생성_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_이름_수정_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_제거_요청;
import static com.mallang.acceptance.post.PostCategoryAcceptanceSteps.카테고리_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.presentation.request.CreatePostCategoryRequest;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.query.response.PostCategoryResponse;
import com.mallang.post.query.response.PostDetailResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 카테고리 인수테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostCategoryAcceptanceTest extends AcceptanceTest {

    private String 말랑_세션_ID;
    private String 동훈_세션_ID;
    private String 말랑_블로그_이름;
    private String 동훈_블로그_이름;
    private CreatePostCategoryRequest Spring_카테고리_생성_요청;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        동훈_블로그_이름 = 블로그_개설(동훈_세션_ID, "donghun-log");
        Spring_카테고리_생성_요청 = new CreatePostCategoryRequest(
                말랑_블로그_이름,
                "Spring",
                null,
                null,
                null
        );
    }

    @Nested
    class 카테고리_생성_API {

        @Test
        void 카테고리를_생성한다() {
            // when
            var 응답 = 카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 하위_카테고리를_생성한다() {
            // given
            var 상위_카테고리_생성_응답 = 카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청);
            var 상위_카테고리_ID = ID를_추출한다(상위_카테고리_생성_응답);
            var JPA_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "Jpa",
                    상위_카테고리_ID,
                    null,
                    null
            );

            // when
            var 응답 = 카테고리_생성_요청(말랑_세션_ID, JPA_카테고리_생성_요청);

            // then
            응답_상태를_검증한다(응답, 생성됨);
            값이_존재한다(ID를_추출한다(응답));
        }

        @Test
        void 다른_사람의_블로그에_카테고리_생성_시_예외() {
            // given
            var 카테고리_생성_요청 = new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "Jpa",
                    null,
                    null,
                    null
            );

            // when
            var 응답 = 카테고리_생성_요청(동훈_세션_ID, 카테고리_생성_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 타인의_카테고리_계층에_참여하려는_경우_예외() {
            // given
            var 상위_카테고리_생성_응답 = 카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청);
            var 상위_카테고리_ID = ID를_추출한다(상위_카테고리_생성_응답);
            var JPA_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    동훈_블로그_이름,
                    "Jpa",
                    상위_카테고리_ID,
                    null,
                    null
            );

            // when
            var 응답 = 카테고리_생성_요청(동훈_세션_ID, JPA_카테고리_생성_요청);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 카테고리_계층구조_수정_API {

        @Test
        void 카테고리_게층구조를_업데이트한다() {
            // given
            var Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));
            var NODE_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "NODE",
                    null,
                    Spring_카테고리_ID,
                    null
            );
            var NODE_카테고리_ID = 카테고리_생성(말랑_세션_ID, NODE_카테고리_생성_요청);

            // when
            var 응답 = 카테고리_계층구조_수정_요청(
                    말랑_세션_ID,
                    NODE_카테고리_ID,
                    null,
                    null,
                    Spring_카테고리_ID
            );

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_카테고리가_아니라면_수정할_수_없다() {
            // given
            var Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));
            var NODE_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "NODE",
                    null,
                    null,
                    Spring_카테고리_ID
            );
            var NODE_카테고리_ID = 카테고리_생성(말랑_세션_ID, NODE_카테고리_생성_요청);

            // when
            var 응답 = 카테고리_계층구조_수정_요청(
                    동훈_세션_ID,
                    NODE_카테고리_ID,
                    null,
                    Spring_카테고리_ID,
                    null
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 타인의_카테고리_계층으로_옮길_수_없다() {
            // given
            var Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));
            var NODE_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    동훈_블로그_이름,
                    "NODE",
                    null,
                    null,
                    null
            );
            var NODE_카테고리_ID = 카테고리_생성(동훈_세션_ID, NODE_카테고리_생성_요청);

            // when
            var 응답 = 카테고리_계층구조_수정_요청(
                    동훈_세션_ID,
                    NODE_카테고리_ID,
                    null,
                    Spring_카테고리_ID,
                    null
            );

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 카테고리_이름_수정_API {

        @Test
        void 카테고리_이름을_업데이트한다() {
            // given
            var Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));

            // when
            var 응답 = 카테고리_이름_수정_요청(말랑_세션_ID, Spring_카테고리_ID, "Node");

            // then
            응답_상태를_검증한다(응답, 정상_처리);
        }

        @Test
        void 자신의_카테고리가_아니라면_수정할_수_없다() {
            // given
            var Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));

            // when
            var 응답 = 카테고리_이름_수정_요청(동훈_세션_ID, Spring_카테고리_ID, "Node");

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 카테고리_삭제_API {

        private Long Spring_카테고리_ID;
        private Long JPA_카테고리_ID;

        @BeforeEach
        void setUp() {
            Spring_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, Spring_카테고리_생성_요청));
            var JPA_카테고리_생성_요청 = new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "JPA",
                    Spring_카테고리_ID,
                    null,
                    null
            );
            JPA_카테고리_ID = ID를_추출한다(카테고리_생성_요청(말랑_세션_ID, JPA_카테고리_생성_요청));
        }

        @Test
        void 카테고리는_제거되며_해당_카테고리를_가진_포스트는_카테고리_없음_상태가_된다() {
            // given
            CreatePostRequest 포스트_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "제목",
                    "인트로", "내용",
                    null,
                    PUBLIC,
                    null,
                    JPA_카테고리_ID,
                    emptyList()
            );
            var 포스트_ID = 포스트_생성(말랑_세션_ID, 포스트_생성_요청);

            // when
            var 응답 = 카테고리_제거_요청(말랑_세션_ID, JPA_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 본문_없음);
            var 포스트_조회_응답 = 포스트_단일_조회_요청(null, 포스트_ID, 말랑_블로그_이름, null)
                    .as(PostDetailResponse.class);
            assertThat(포스트_조회_응답.category().categoryId()).isNull();
        }

        @Test
        void 하위_카테고리가_있다면_제거할_수_없다() {
            // when
            var 응답 = 카테고리_제거_요청(말랑_세션_ID, Spring_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 잘못된_요청);
        }

        @Test
        void 자신의_카테고리가_아니라면_제거할_수_없다() {
            // when
            var 응답 = 카테고리_제거_요청(동훈_세션_ID, Spring_카테고리_ID);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }
    }

    @Nested
    class 카테고리_조회_API {

        @Test
        void 특정_블로그의_카테고리를_조회한다() {
            // given
            Long 다른사람_NODE_카테고리_ID = 카테고리_생성(
                    동훈_세션_ID,
                    동훈_블로그_이름,
                    "Node",
                    null,
                    null,
                    null
            );

            var Spring_카테고리_ID = 카테고리_생성(말랑_세션_ID, Spring_카테고리_생성_요청);
            var JPA_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "JPA",
                    Spring_카테고리_ID,
                    null,
                    null
            ));
            var N1_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "N + 1",
                    JPA_카테고리_ID,
                    null,
                    null
            ));
            var Security_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "Security",
                    Spring_카테고리_ID,
                    null,
                    JPA_카테고리_ID
            ));
            var OAuth_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "OAuth",
                    Security_카테고리_ID,
                    null,
                    null
            ));
            var CSRF_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "CSRF",
                    Security_카테고리_ID,
                    null,
                    OAuth_카테고리_ID
            ));
            var Algorithm_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "Algorithm",
                    없음(),
                    Spring_카테고리_ID,
                    null
            ));
            var DFS_카테고리_ID = 카테고리_생성(말랑_세션_ID, new CreatePostCategoryRequest(
                    말랑_블로그_이름,
                    "DFS",
                    Algorithm_카테고리_ID,
                    null,
                    null
            ));
            var 예상_응답 = List.of(
                    new PostCategoryResponse(Spring_카테고리_ID, "Spring", List.of(
                            new PostCategoryResponse(Security_카테고리_ID, "Security", List.of(
                                    new PostCategoryResponse(CSRF_카테고리_ID, "CSRF", emptyList()),
                                    new PostCategoryResponse(OAuth_카테고리_ID, "OAuth", emptyList())
                            )),
                            new PostCategoryResponse(JPA_카테고리_ID, "JPA", List.of(
                                    new PostCategoryResponse(N1_카테고리_ID, "N + 1", emptyList())
                            ))
                    )),
                    new PostCategoryResponse(Algorithm_카테고리_ID, "Algorithm", List.of(
                            new PostCategoryResponse(DFS_카테고리_ID, "DFS", emptyList())
                    ))
            );

            // when
            var 응답 = 블로그의_카테고리_조회_요청(말랑_블로그_이름);

            // then
            카테고리_조회_응답을_검증한다(응답, 예상_응답);
        }
    }
}




