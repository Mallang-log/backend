package com.mallang.post.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostFixture.privatePost;
import static com.mallang.post.PostFixture.protectedPost;
import static com.mallang.post.PostFixture.publicPost;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostManageSearchDao.PostManageSearchCond;
import com.mallang.post.query.repository.PostQueryRepository;
import com.mallang.post.query.response.PostManageDetailResponse;
import com.mallang.post.query.response.PostManageDetailResponse.CategoryResponse;
import com.mallang.post.query.response.PostManageDetailResponse.TagResponses;
import com.mallang.post.query.response.PostManageSearchResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("포스트 관리용 조회 서비스 (PostManageQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostManageQueryServiceTest {

    private final BlogQueryRepository blogQueryRepository = mock(BlogQueryRepository.class);
    private final PostQueryRepository postQueryRepository = mock(PostQueryRepository.class);
    private final MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);
    private final PostManageQueryService postManageQueryService = new PostManageQueryService(
            blogQueryRepository,
            postQueryRepository,
            memberQueryRepository
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(member);

    @BeforeEach
    void setUp() {
        given(memberQueryRepository.getById(member.getId())).willReturn(member);
        given(memberQueryRepository.getById(other.getId())).willReturn(other);
        given(blogQueryRepository.getByName(blog.getName())).willReturn(blog);
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 포스트_작성자가_아니라면_예외() {
            // given
            Post post = publicPost(1L, blog);
            given(postQueryRepository.getById(post.getId().getPostId(), blog.getName())).willReturn(post);

            // when & then
            assertThatThrownBy(() ->
                    postManageQueryService.getById(other.getId(), post.getId().getPostId(), blog.getName())
            ).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 포스트의_모든_정보를_조회한다() {
            // given
            Post post = protectedPost(1L, blog, "1234");
            given(postQueryRepository.getById(post.getId().getPostId(), blog.getName())).willReturn(post);

            // when
            PostManageDetailResponse response =
                    postManageQueryService.getById(member.getId(), post.getId().getPostId(), blog.getName());

            // then
            assertThat(response).usingRecursiveComparison()
                    .ignoringFields("createdDate")
                    .isEqualTo(new PostManageDetailResponse(
                            post.getId().getPostId(),
                            "title",
                            "intro",
                            "content",
                            "image",
                            PROTECTED,
                            "1234",
                            null,
                            new CategoryResponse(null, null),
                            new TagResponses(Collections.emptyList()))
                    );
        }
    }

    @Nested
    class 포스트_검색_시 {

        private final Pageable pageable = PageRequest.of(0, 10);

        @Test
        void 블로그_주인이_아니면_예외() {
            // given
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, null);

            // when & then
            assertThatThrownBy(() ->
                    postManageQueryService.search(
                            other.getId(),
                            blog.getName(),
                            cond,
                            pageable
                    )
            ).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 포스트들을_조건에_맞게_조회한다() {
            // given
            Post post1 = protectedPost(1L, blog, "1234");
            Post post2 = privatePost(2L, blog);
            PageImpl<Post> result = new PageImpl<>(
                    List.of(post1, post2),
                    pageable,
                    2
            );
            PostManageSearchCond cond = new PostManageSearchCond(null, null, null, null);
            given(postQueryRepository.searchForManage(blog, cond, pageable))
                    .willReturn(result);

            // when
            Page<PostManageSearchResponse> responses = postManageQueryService.search(
                    member.getId(),
                    blog.getName(),
                    cond,
                    pageable
            );

            // then
            assertThat(responses).hasSize(2);
        }
    }
}
