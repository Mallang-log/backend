package com.mallang.post.query;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.command.CreateCategoryCommand;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.exception.BadPostSearchCondException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.query.dao.PostSearchDao.PostSearchCond;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.post.query.response.PostSearchResponse;
import com.mallang.post.query.response.PostSearchResponse.CategoryResponse;
import com.mallang.post.query.response.PostSearchResponse.TagResponses;
import com.mallang.post.query.response.PostSearchResponse.WriterResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 조회 서비스 (PostQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostQueryServiceTest extends ServiceTest {

    private Long mallangId;
    private Long donghunId;
    private String mallangBlogName;
    private String donghunBlogName;

    private CreatePostCommand 말랑_public_포스트_작성_요청;
    private CreatePostCommand 말랑_protected_포스트_작성_요청;
    private CreatePostCommand 말랑_private_포스트_작성_요청;
    private CreatePostCommand 동훈_public_포스트_작성_요청;
    private CreatePostCommand 동훈_protected_포스트_작성_요청;
    private CreatePostCommand 동훈_private_포스트_작성_요청;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("말랑");
        donghunId = 회원을_저장한다("동훈");
        mallangBlogName = 블로그_개설(mallangId, "mallang-log");
        donghunBlogName = 블로그_개설(donghunId, "donghun-log");
        말랑_public_포스트_작성_요청 = new CreatePostCommand(
                mallangId,
                mallangBlogName,
                "mallang-public",
                "mallang-public",
                null,
                "mallang-public",
                PUBLIC,
                null,
                null,
                Collections.emptyList()
        );
        말랑_protected_포스트_작성_요청 = new CreatePostCommand(
                mallangId,
                mallangBlogName,
                "mallang-protected",
                "mallang-protected",
                null,
                "mallang-protected",
                PROTECTED,
                "1234",
                null,
                Collections.emptyList()
        );
        말랑_private_포스트_작성_요청 = new CreatePostCommand(
                mallangId,
                mallangBlogName,
                "mallang-private",
                "mallang-private",
                null,
                "mallang-private",
                PRIVATE,
                null,
                null,
                Collections.emptyList()
        );
        동훈_public_포스트_작성_요청 = new CreatePostCommand(
                donghunId,
                donghunBlogName,
                "donghun-public",
                "donghun-public",
                null,
                "donghun-public",
                PUBLIC,
                null,
                null,
                Collections.emptyList()
        );
        동훈_protected_포스트_작성_요청 = new CreatePostCommand(
                donghunId,
                donghunBlogName,
                "donghun-protected",
                "donghun-protected",
                null,
                "donghun-protected",
                PROTECTED,
                "1234",
                null,
                Collections.emptyList()
        );
        동훈_private_포스트_작성_요청 = new CreatePostCommand(
                donghunId,
                donghunBlogName,
                "donghun-private",
                "donghun-private",
                null,
                "donghun-private",
                PRIVATE,
                null,
                null,
                Collections.emptyList()
        );
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 포스트를_조회한다() {
            // given
            Long id = postService.create(말랑_public_포스트_작성_요청).getId();

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(id, mallangBlogName, null, null);

            // then
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("createdDate")
                    .isEqualTo(PostDetailResponse.builder()
                            .postId(id)
                            .title("mallang-public")
                            .content("mallang-public")
                            .visibility(PUBLIC)
                            .writer(new PostDetailResponse.WriterResponse(mallangId, "말랑", "말랑"))
                            .build());
        }

        @Test
        void 좋아요_눌렀는지_여부가_반영된다() {
            // given
            Long id = postService.create(말랑_public_포스트_작성_요청).getId();
            postLikeService.like(new ClickPostLikeCommand(id, mallangBlogName, mallangId, null));

            // when
            PostDetailResponse responseClickLike = postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId,
                    null);
            PostDetailResponse responseNoLike = postQueryService.getByIdAndBlogName(id, mallangBlogName, null, null);

            // then
            assertThat(responseClickLike.isLiked()).isTrue();
            assertThat(responseNoLike.isLiked()).isFalse();
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // given
            Long id = postService.create(말랑_private_포스트_작성_요청).getId();

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId, null);

            // then
            assertThat(response.postId()).isEqualTo(id);
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // given
            Long id = postService.create(말랑_private_포스트_작성_요청).getId();

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId + 1, null)
            ).isInstanceOf(NoAuthorityAccessPostException.class);
        }

        @Test
        void 블로그_주인은_보호글을_볼_수_있다() {
            // given
            Long id = postService.create(말랑_protected_포스트_작성_요청).getId();

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId, null);

            // then
            assertThat(response.postId()).isEqualTo(id);
            assertThat(response.title()).isEqualTo("mallang-protected");
            assertThat(response.content()).isEqualTo("mallang-protected");
        }

        @Test
        void 블로그_주인이_아닌_경우_비밀번호가_일치하면_보호글을_볼_수_있다() {
            // given
            Long id = postService.create(말랑_protected_포스트_작성_요청).getId();

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId + 1,
                    "1234");

            // then
            assertThat(response.postId()).isEqualTo(id);
            assertThat(response.title()).isEqualTo("mallang-protected");
            assertThat(response.content()).isEqualTo("mallang-protected");
        }

        @Test
        void 블로그_주인이_아니며_비밀번호가_일치하지_않는_경우_보호글_조회시_내용이_보호된다() {
            // given
            Long id = postService.create(말랑_protected_포스트_작성_요청).getId();

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(id, mallangBlogName, mallangId + 1, null);

            // then
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("createdDate", "blogId")
                    .isEqualTo(PostDetailResponse.builder()
                            .postId(id)
                            .title("mallang-protected")
                            .content("보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.")
                            .visibility(PROTECTED)
                            .isProtected(true)
                            .writer(new PostDetailResponse.WriterResponse(mallangId, "말랑", "말랑"))
                            .build());
        }
    }

    @Nested
    class 포스트_검색_시 {

        @Test
        void 포스트를_전체_조회한다() {
            // given
            Long post1Id = postService.create(말랑_public_포스트_작성_요청).getId();
            Long post2Id = postService.create(말랑_public_포스트_작성_요청).getId();
            PostSearchCond cond = PostSearchCond.builder().build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).hasSize(2)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(post2Id, post1Id);
        }

        @Test
        void 비공개_포스트인_경우_주인에게만_조회되며_나머지_포스트는_모든_사람이_조회할_수_있다() {
            // given
            postService.create(말랑_public_포스트_작성_요청);
            postService.create(말랑_protected_포스트_작성_요청);
            postService.create(말랑_private_포스트_작성_요청);
            postService.create(동훈_public_포스트_작성_요청);
            postService.create(동훈_protected_포스트_작성_요청);
            postService.create(동훈_private_포스트_작성_요청);

            // when
            List<PostSearchResponse> search = postQueryService.search(new PostSearchCond(null, null, null, null,
                            null, null, null), pageable, mallangId
                    )
                    .getContent();

            // then
            assertThat(search)
                    .extracting(PostSearchResponse::title)
                    .containsExactly("donghun-protected", "donghun-public",
                            "mallang-private", "mallang-protected", "mallang-public");
            assertThat(search)
                    .extracting(PostSearchResponse::content)
                    .containsExactly("보호되어 있는 글입니다.", "donghun-public",
                            "mallang-private", "mallang-protected", "mallang-public");
        }

        @Test
        void 특정_카테고리의_포스트만_조회한다() {
            // given
            Long 스프링 = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogName, "스프링", null));
            Long 노드 = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogName, "노드", null));
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "content1", 스프링).getId();
            포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "content2", 노드);
            PostSearchCond cond = PostSearchCond.builder()
                    .categoryId(스프링)
                    .blogName(mallangBlogName)
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post1Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .category(new CategoryResponse(스프링, "스프링"))
                                            .build()
                            )
                    );
        }

        @Test
        void 최상위_카테고리로_조회_시_하위_카테고리도_포함되면_조회한다() {
            // given
            Long 스프링 = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogName, "스프링", null));
            Long JPA = categoryService.create(new CreateCategoryCommand(mallangId, mallangBlogName, "JPA", 스프링));
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "content1", 스프링).getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "content2", JPA).getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .categoryId(스프링)
                    .blogName(mallangBlogName)
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post2Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("content2")
                                            .category(new CategoryResponse(JPA, "JPA"))
                                            .build(),
                                    PostSearchResponse.builder()
                                            .id(post1Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .category(new CategoryResponse(스프링, "스프링"))
                                            .build()
                            )
                    );
        }

        @Test
        void 특정_태그의_포스트만_조회한다() {
            // given
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "content1", "tag1").getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "content2", "tag1", "tag2")
                    .getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .tag("tag2")
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post2Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("content2")
                                            .tags(new TagResponses(List.of("tag1", "tag2")))
                                            .build()
                            )
                    );
        }

        @Test
        void 특정_작성자의_포스트만_조회한다() {
            // given
            Long findWriterId = 회원을_저장한다("말랑말랑");
            String otherBlogName = 블로그_개설(findWriterId, "other");
            Long post1Id = 포스트를_저장한다(findWriterId, otherBlogName, "포스트1", "content1", "tag1").getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "content2", "tag1", "tag2")
                    .getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .writerId(findWriterId)
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post1Id)
                                            .writer(new WriterResponse(findWriterId, "말랑말랑", "말랑말랑"))
                                            .title("포스트1")
                                            .content("content1")
                                            .tags(new TagResponses(List.of("tag1")))
                                            .build()
                            )
                    );
        }

        @Test
        void 제목으로_조회() {
            // given
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "안녕").getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "안녕하세요").getId();
            Long post3Id = 포스트를_저장한다(mallangId, mallangBlogName, "안녕", "히히").getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .title("안녕")
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post3Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("안녕")
                                            .content("히히")
                                            .build()
                            )
                    );
        }

        @Test
        void 내용으로_조회() {
            // given
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "안녕").getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "안녕하세요").getId();
            Long post3Id = 포스트를_저장한다(mallangId, mallangBlogName, "안녕", "히히").getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .content("안녕")
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post2Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("안녕하세요")
                                            .build(),
                                    PostSearchResponse.builder()
                                            .id(post1Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("안녕")
                                            .build()
                            )
                    );
        }

        @DisplayName("내용 + 제목으로 조회")
        @Test
        void 내용_and_제목으로_조회() {
            // given
            Long post1Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트1", "안녕").getId();
            Long post2Id = 포스트를_저장한다(mallangId, mallangBlogName, "포스트2", "안녕하세요").getId();
            Long post3Id = 포스트를_저장한다(mallangId, mallangBlogName, "안녕히", "히히").getId();
            PostSearchCond cond = PostSearchCond.builder()
                    .titleOrContent("안녕")
                    .build();

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(List.of(
                                    PostSearchResponse.builder()
                                            .id(post3Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("안녕히")
                                            .content("히히")
                                            .build(),
                                    PostSearchResponse.builder()
                                            .id(post2Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트2")
                                            .content("안녕하세요")
                                            .build(),
                                    PostSearchResponse.builder()
                                            .id(post1Id)
                                            .writer(new WriterResponse(mallangId, "말랑", "말랑"))
                                            .title("포스트1")
                                            .content("안녕")
                                            .build()
                            )
                    );
        }

        @DisplayName("제목이나 내용이 있는데 제목 + 내용도 있다면 예외")
        @Test
        void 제목이나_내용이_있는데_제목_and_내용도_있다면_예외() {
            // given
            PostSearchCond cond = PostSearchCond.builder()
                    .title("1")
                    .titleOrContent("안녕")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.search(cond, pageable, null)
            ).isInstanceOf(BadPostSearchCondException.class);
        }
    }
}
