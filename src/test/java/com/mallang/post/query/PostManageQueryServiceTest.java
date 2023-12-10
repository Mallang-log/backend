package com.mallang.post.query;

import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.ServiceTest;
import com.mallang.post.application.command.CreatePostCategoryCommand;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageDetailResponse.CategoryResponse;
import com.mallang.post.query.response.PostManageDetailResponse.TagResponses;
import com.mallang.post.query.response.PostManageSearchResponse;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

@DisplayName("포스트 관리용 조회 서비스 (PostManageQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostManageQueryServiceTest extends ServiceTest {

    private Long mallangId;
    private Long donghunId;
    private String mallangBlogName;

    @BeforeEach
    void setUp() {
        mallangId = 회원을_저장한다("말랑");
        donghunId = 회원을_저장한다("동훈");
        mallangBlogName = 블로그_개설(mallangId, "mallang-log");
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 보호_글의_경우_모든_정보를_보여준다() {
            // given
            var 말랑_protected_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-protected",
                    "mallang-protected", "mallang-protected",
                    null,
                    PROTECTED,
                    "1234",
                    null,
                    Collections.emptyList()
            );
            PostId postId = postService.create(말랑_protected_포스트_작성_요청);

            // when
            PostManageDetailResponse response =
                    postManageQueryService.getById(mallangId, postId.getPostId(), mallangBlogName);

            // then
            assertThat(response).usingRecursiveComparison()
                    .ignoringFields("createdDate")
                    .isEqualTo(new PostManageDetailResponse(
                            postId.getPostId(),
                            "mallang-protected",
                            "mallang-protected",
                            "mallang-protected",
                            null,
                            PROTECTED,
                            "1234",
                            null,
                            new CategoryResponse(null, null),
                            new TagResponses(Collections.emptyList()))
                    );
        }

        @Test
        void 비밀_글도_조회_가능하다() {
            // given
            var 말랑_private_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-private",
                    "mallang-private", "mallang-private",
                    null,
                    PRIVATE,
                    null,
                    null,
                    Collections.emptyList()
            );
            PostId postId = postService.create(말랑_private_포스트_작성_요청);

            // when
            PostManageDetailResponse response =
                    postManageQueryService.getById(mallangId, postId.getPostId(), mallangBlogName);

            // then
            assertThat(response).isNotNull();
        }

        @Test
        void 포스트_작성자가_아니라면_예외() {
            // given
            var 말랑_public_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-public",
                    "mallang-public", "mallang-public",
                    null,
                    PUBLIC,
                    null,
                    null,
                    Collections.emptyList()
            );
            PostId postId = postService.create(말랑_public_포스트_작성_요청);

            // when & then
            assertThatThrownBy(() ->
                    postManageQueryService.getById(donghunId, postId.getPostId(), mallangBlogName)
            ).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 포스트_검색_시 {

        private CreatePostCommand 말랑_public_포스트_작성_요청;
        private CreatePostCommand 말랑_protected_포스트_작성_요청;
        private CreatePostCommand 말랑_private_포스트_작성_요청;
        private Long 스프링_카테고리_ID;

        @BeforeEach
        void setUp() {
            스프링_카테고리_ID = postCategoryService.create(
                    new CreatePostCategoryCommand(mallangId, mallangBlogName, "스프링", null, null, null)
            );
            말랑_public_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-public",
                    "mallang-public", "mallang-public",
                    null,
                    PUBLIC,
                    null,
                    null,
                    Collections.emptyList()
            );
            말랑_protected_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-protected",
                    "mallang-protected", "mallang-protected",
                    null,
                    PROTECTED,
                    "1234",
                    스프링_카테고리_ID,
                    Collections.emptyList()
            );
            말랑_private_포스트_작성_요청 = new CreatePostCommand(
                    mallangId,
                    mallangBlogName,
                    "mallang-private",
                    "mallang-private", "mallang-private",
                    null,
                    PRIVATE,
                    null,
                    null,
                    Collections.emptyList()
            );
            postService.create(말랑_public_포스트_작성_요청);
            postService.create(말랑_protected_포스트_작성_요청);
            postService.create(말랑_private_포스트_작성_요청);
        }

        @Test
        void 아무_조건이_없으면_모두_조회된다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, null);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(3);
        }

        @Test
        void 제목으로_검색한다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond("protect", null, null, null);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(1);
        }

        @Test
        void 내용으로_검색한다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, "pri", null, null);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(1);
        }

        @Test
        void 카테고리로_검색한다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, 스프링_카테고리_ID, null);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(1);
        }

        @Test
        void 공개범위로_검색한다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, PRIVATE);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(1);
        }

        @Test
        void 보호_글의_경우_비밀번호가_보인다() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, null);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    mallangId,
                    mallangBlogName,
                    cond,
                    pageable
            );

            // then
            PostManageSearchResponse postManageSearchResponse = responses.getContent().get(1);
            assertThat(postManageSearchResponse.password()).isEqualTo("1234");
        }


        @Test
        void 블로그_주인이_아니면_예외() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, null);

            // when & then
            assertThatThrownBy(() ->
                    postManageQueryService.search(
                            donghunId,
                            mallangBlogName,
                            cond,
                            pageable
                    )
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }
}
