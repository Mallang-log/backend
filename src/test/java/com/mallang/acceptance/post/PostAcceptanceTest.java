package com.mallang.acceptance.post;

import static com.mallang.acceptance.AcceptanceSteps.권한_없음;
import static com.mallang.acceptance.AcceptanceSteps.응답_상태를_검증한다;
import static com.mallang.acceptance.AcceptanceSteps.찾을수_없음;
import static com.mallang.acceptance.auth.AuthAcceptanceSteps.회원가입과_로그인_후_세션_ID_반환;
import static com.mallang.acceptance.blog.BlogAcceptanceSteps.블로그_개설;
import static com.mallang.acceptance.category.CategoryAcceptanceSteps.카테고리_생성;
import static com.mallang.acceptance.post.PostAcceptanceSteps.보호됨;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_단일_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_요청;
import static com.mallang.acceptance.post.PostAcceptanceSteps.포스트_전체_조회_응답을_검증한다;
import static com.mallang.acceptance.post.PostLikeAcceptanceSteps.포스트_좋아요_요청;
import static com.mallang.acceptance.post.PostManageAcceptanceSteps.포스트_생성;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.acceptance.AcceptanceTest;
import com.mallang.post.presentation.request.CreatePostRequest;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostDetailResponse.CategoryResponse;
import com.mallang.post.query.response.PostDetailResponse.TagResponses;
import com.mallang.post.query.response.PostDetailResponse.WriterResponse;
import com.mallang.post.query.response.PostSearchResponse;
import io.restassured.common.mapper.TypeRef;
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
    private Long 말랑_카테고리_ID;
    private Long 동훈_카테고리_ID;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        말랑_세션_ID = 회원가입과_로그인_후_세션_ID_반환("말랑");
        동훈_세션_ID = 회원가입과_로그인_후_세션_ID_반환("동훈");
        말랑_블로그_이름 = 블로그_개설(말랑_세션_ID, "mallang-log");
        동훈_블로그_이름 = 블로그_개설(동훈_세션_ID, "donghun-log");
        말랑_카테고리_ID = 카테고리_생성(말랑_세션_ID, 말랑_블로그_이름, "Spring", null);
        동훈_카테고리_ID = 카테고리_생성(동훈_세션_ID, 동훈_블로그_이름, "Spring", null);
    }

    @Nested
    class 포스트_단일_조회_API {

        private Long 공개_포스트_ID;
        private Long 보호_포스트_ID;
        private Long 비공개_포스트_ID;

        @BeforeEach
        void setUp() {
            CreatePostRequest 공개_포스트_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[공개] 제목",
                    "[공개] 내용",
                    "[공개] 섬네일",
                    "[공개] 포스트 인트로 입니다.",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    List.of("[공개] 태그")
            );
            CreatePostRequest 보호_포스트_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[보호] 제목",
                    "[보호] 내용",
                    "[보호] 섬네일",
                    "[보호] 포스트 인트로 입니다.",
                    PROTECTED,
                    "1234",
                    말랑_카테고리_ID,
                    List.of("[보호] 태그")
            );
            CreatePostRequest 비공개_포스트_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[비공개] 제목",
                    "[비공개] 내용",
                    "[비공개] 섬네일",
                    "[비공개] 포스트 인트로 입니다.",
                    PRIVATE,
                    null,
                    말랑_카테고리_ID,
                    List.of("[비공개] 태그")
            );
            공개_포스트_ID = 포스트_생성(말랑_세션_ID, 공개_포스트_요청);
            보호_포스트_ID = 포스트_생성(말랑_세션_ID, 보호_포스트_요청);
            비공개_포스트_ID = 포스트_생성(말랑_세션_ID, 비공개_포스트_요청);
        }

        @Test
        void 포스트를_단일_조회한다() {
            // when
            var 응답 = 포스트_단일_조회_요청(null, 공개_포스트_ID, null);

            // then
            PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
            assertThat(postDetailResponse.id()).isEqualTo(공개_포스트_ID);
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
            포스트_좋아요_요청(말랑_세션_ID, 공개_포스트_ID, null);

            // when
            var 좋아요_눌린_응답 = 포스트_단일_조회_요청(말랑_세션_ID, 공개_포스트_ID, null);
            var 좋아요_안눌린_응답 = 포스트_단일_조회_요청(null, 공개_포스트_ID, null);

            // then
            assertThat(좋아요_눌린_응답.as(PostDetailResponse.class).isLiked()).isTrue();
            assertThat(좋아요_안눌린_응답.as(PostDetailResponse.class).isLiked()).isFalse();
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 비공개_포스트_ID, null);

            // then
            PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
            assertThat(postDetailResponse.id()).isEqualTo(비공개_포스트_ID);
            assertThat(postDetailResponse.content()).isEqualTo("[비공개] 내용");
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // when
            var 응답 = 포스트_단일_조회_요청(null, 비공개_포스트_ID, null);

            // then
            응답_상태를_검증한다(응답, 권한_없음);
        }

        @Test
        void 블로그_주인은_보호글을_볼_수_있다() {
            // when
            var 응답 = 포스트_단일_조회_요청(말랑_세션_ID, 보호_포스트_ID, null);

            // then
            PostDetailResponse postDetailResponse = 응답.as(PostDetailResponse.class);
            assertThat(postDetailResponse.id()).isEqualTo(보호_포스트_ID);
            assertThat(postDetailResponse.isProtected()).isFalse();
            assertThat(postDetailResponse.content()).isEqualTo("[보호] 내용");
            assertThat(postDetailResponse.password()).isNull();
        }

        @Test
        void 블로그_주인이_아닌_경우_보호글_조회시_내용과_썸네일_이미지가_보호된다() {
            // when
            var 응답 = 포스트_단일_조회_요청(null, 보호_포스트_ID, null);

            // then
            포스트_단일_조회_응답을_검증한다(응답,
                    new PostDetailResponse(
                            보호_포스트_ID,
                            "[보호] 제목",
                            "보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.",
                            "",
                            PROTECTED,
                            보호됨,
                            null,
                            0,
                            false,
                            null,
                            new WriterResponse(null, "말랑", "말랑"),
                            new CategoryResponse(말랑_카테고리_ID, "Spring"),
                            new TagResponses(List.of("[보호] 태그"))
                    ));
        }
    }

    @Nested
    class 포스트_검색_API {

        @Test
        void 포스트를_전체_조회한다() {
            // given
            CreatePostRequest 포스트_작성_요청1 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[공개] 제목",
                    "[공개] 내용",
                    "[공개] 섬네일",
                    "[공개] 포스트 인트로 입니다.",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    List.of("[공개] 태그")
            );
            CreatePostRequest 포스트_작성_요청2 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[공개] 제목2",
                    "[공개] 내용2",
                    "[공개] 섬네일2",
                    "[공개] 포스트 인트로 입니다.2",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    List.of("[공개] 태그2")
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트_작성_요청1);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트_작성_요청2);

            // when
            var 응답 = 포스트_전체_조회_요청(null, null, null, null, null, null, null);

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(포스트2_ID, 포스트1_ID);
        }

        @Test
        void 비공개_포스트인_경우_주인에게만_조회되며_나머지_포스트는_모든_사람이_조회할_수_있다() {
            // given
            CreatePostRequest 말랑_공개_포스트_작성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[공개] 말랑 제목",
                    "[공개] 말랑 내용",
                    "[공개] 말랑 썸네일",
                    "[공개] 말랑 인트로",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            CreatePostRequest 말랑_보호_포스트_작성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[보호] 말랑 제목",
                    "[보호] 말랑 내용",
                    "[보호] 말랑 썸네일",
                    "[보호] 말랑 인트로",
                    PROTECTED,
                    "1234",
                    말랑_카테고리_ID,
                    emptyList()
            );
            CreatePostRequest 말랑_비공개_포스트_작성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "[비공개] 말랑 제목",
                    "[비공개] 말랑 내용",
                    "[비공개] 말랑 썸네일",
                    "[비공개] 말랑 인트로",
                    PRIVATE,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            CreatePostRequest 동훈_공개_포스트_작성_요청 = new CreatePostRequest(
                    동훈_블로그_이름,
                    "[공개] 동훈 제목",
                    "[공개] 동훈 내용",
                    "[공개] 동훈 썸네일",
                    "[공개] 동훈 인트로",
                    PUBLIC,
                    null,
                    동훈_카테고리_ID,
                    emptyList()
            );
            CreatePostRequest 동훈_보호_포스트_작성_요청 = new CreatePostRequest(
                    동훈_블로그_이름,
                    "[보호] 동훈 제목",
                    "[보호] 동훈 내용",
                    "[보호] 동훈 썸네일",
                    "[보호] 동훈 인트로",
                    PROTECTED,
                    "1234",
                    동훈_카테고리_ID,
                    emptyList()
            );
            CreatePostRequest 동훈_비공개_포스트_작성_요청 = new CreatePostRequest(
                    동훈_블로그_이름,
                    "[비공개] 동훈 제목",
                    "[비공개] 동훈 내용",
                    "[비공개] 동훈 썸네일",
                    "[비공개] 동훈 인트로",
                    PRIVATE,
                    null,
                    동훈_카테고리_ID,
                    emptyList()
            );

            Long 말랑_공개_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_공개_포스트_작성_요청);
            Long 말랑_보호_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_보호_포스트_작성_요청);
            Long 말랑_비공개_포스트_ID = 포스트_생성(말랑_세션_ID, 말랑_비공개_포스트_작성_요청);

            Long 동훈_공개_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_공개_포스트_작성_요청);
            Long 동훈_보호_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_보호_포스트_작성_요청);
            Long 동훈_비공개_포스트_ID = 포스트_생성(동훈_세션_ID, 동훈_비공개_포스트_작성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(동훈_세션_ID, null, null, null, null, null, null, null);

            // then
            var 예상_데이터 = List.of(
                    new PostSearchResponse(
                            동훈_비공개_포스트_ID,
                            "[비공개] 동훈 제목",
                            "[비공개] 동훈 내용",
                            "[비공개] 동훈 인트로",
                            "[비공개] 동훈 썸네일",
                            PRIVATE,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "동훈", "동훈"),
                            new PostSearchResponse.CategoryResponse(동훈_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    ),
                    new PostSearchResponse(
                            동훈_보호_포스트_ID,
                            "[보호] 동훈 제목",
                            "[보호] 동훈 내용",
                            "[보호] 동훈 인트로",
                            "[보호] 동훈 썸네일",
                            PROTECTED,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "동훈", "동훈"),
                            new PostSearchResponse.CategoryResponse(동훈_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    ),
                    new PostSearchResponse(
                            동훈_공개_포스트_ID,
                            "[공개] 동훈 제목",
                            "[공개] 동훈 내용",
                            "[공개] 동훈 인트로",
                            "[공개] 동훈 썸네일",
                            PUBLIC,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "동훈", "동훈"),
                            new PostSearchResponse.CategoryResponse(동훈_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    ),

                    new PostSearchResponse(
                            말랑_보호_포스트_ID,
                            "[보호] 말랑 제목",
                            "보호되어 있는 글입니다.",
                            "",
                            "",
                            PROTECTED,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "말랑", "말랑"),
                            new PostSearchResponse.CategoryResponse(말랑_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    ),
                    new PostSearchResponse(
                            말랑_공개_포스트_ID,
                            "[공개] 말랑 제목",
                            "[공개] 말랑 내용",
                            "[공개] 말랑 인트로",
                            "[공개] 말랑 썸네일",
                            PUBLIC,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "말랑", "말랑"),
                            new PostSearchResponse.CategoryResponse(말랑_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    )
            );
            포스트_전체_조회_응답을_검증한다(응답, 예상_데이터);
        }

        @Test
        void 특정_카테고리로_검색하면_해당_카테고리와_하위_카테고리에_해당하는_포스트가_조회된다() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트1",
                    "이건 첫번째 포스트이네요.",
                    null,
                    "첫 포스트 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트2",
                    "이번에는 이것 저것들에 대해 알아보아요",
                    null,
                    "2 포스트 인트로",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(말랑_카테고리_ID, 말랑_블로그_이름, null, null, null, null, null);

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).id()).isEqualTo(포스트2_ID);
        }

        @Test
        void 태그로_필터링() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트1",
                    "이건 첫번째 포스트이네요.",
                    null,
                    "첫 포스트 인트로",
                    PUBLIC,
                    null,
                    null,
                    List.of("tag1", "tag2", "tag3")
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트2",
                    "이번에는 이것 저것들에 대해 알아보아요",
                    null,
                    "2 포스트 인트로",
                    PUBLIC,
                    null,
                    null,
                    List.of("tag2", "tag3")
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(null, 말랑_블로그_이름, "tag1", null, null, null, null);

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .hasSize(1)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(포스트1_ID);
        }

        @Test
        void 제목으로_검색한다() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트1",
                    "포스트1입니다.",
                    null,
                    "첫 포스트 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스 트2",
                    "포스트2입니다.",
                    null,
                    "2 포스트 인트로",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(null, 말랑_블로그_이름, null, null, "포스트", null, null);

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .hasSize(1)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(포스트1_ID);
        }

        @Test
        void 내용으로_검색한다() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트1",
                    "포스트 1입니다.",
                    null,
                    "첫 포스트 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트 2",
                    "포스트 2입니다.",
                    null,
                    "2 포스트 인트로",
                    PUBLIC,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(null, 말랑_블로그_이름, null, null, null, "포스트 2", null);

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses).hasSize(1);
            assertThat(responses.get(0).id()).isEqualTo(포스트2_ID);
        }

        @Test
        void 내용으로_검색_시_글_작성자가_아니라면_보호글은_조회되나_내용은_감춰지며_비공개는_조회조차_되지_않는다() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트",
                    "포스트",
                    "포스트 이미지",
                    "포스트 인트로",
                    PROTECTED,
                    "1234",
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트",
                    "포스트",
                    "포스트 이미지",
                    "포스트 인트로",
                    PRIVATE,
                    null,
                    말랑_카테고리_ID,
                    emptyList()
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(null, 말랑_블로그_이름, null, null, null, "포스트", null);

            // then
            var 예상 = List.of(
                    new PostSearchResponse(
                            포스트1_ID,
                            "포스트",
                            "보호된 글입니다.",
                            "",
                            "",
                            PROTECTED,
                            0,
                            null,
                            new PostSearchResponse.WriterResponse(null, "말랑", "말랑"),
                            new PostSearchResponse.CategoryResponse(말랑_카테고리_ID, "Spring"),
                            new PostSearchResponse.TagResponses(emptyList())
                    )
            );
        }

        @Test
        void 제목_내용으로_검색한다() {
            // given
            CreatePostRequest 포스트1_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "포스트 1",
                    "1입니다.",
                    null,
                    "첫 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트2_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "2번째",
                    "포스트.",
                    null,
                    "2 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            CreatePostRequest 포스트3_생성_요청 = new CreatePostRequest(
                    말랑_블로그_이름,
                    "3",
                    "3",
                    null,
                    "2 인트로",
                    PUBLIC,
                    null,
                    null,
                    emptyList()
            );
            var 포스트1_ID = 포스트_생성(말랑_세션_ID, 포스트1_생성_요청);
            var 포스트2_ID = 포스트_생성(말랑_세션_ID, 포스트2_생성_요청);
            var 포스트3_ID = 포스트_생성(말랑_세션_ID, 포스트3_생성_요청);

            // when
            var 응답 = 포스트_전체_조회_요청(null, 말랑_블로그_이름, null, null, null, null, "포스트");

            // then
            List<PostSearchResponse> responses = 응답.as(new TypeRef<>() {
            });
            assertThat(responses)
                    .hasSize(2)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(포스트2_ID, 포스트1_ID);
        }
    }
}
