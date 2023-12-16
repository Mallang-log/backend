package com.mallang.post.query;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.PostFixture.privatePost;
import static com.mallang.post.PostFixture.protectedPost;
import static com.mallang.post.PostFixture.publicPost;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.query.repository.PostLikeQueryRepository;
import com.mallang.post.query.repository.PostQueryRepository;
import com.mallang.post.query.repository.PostSearchDao.PostSearchCond;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DisplayName("포스트 조회 서비스 (PostQueryService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostQueryServiceTest {

    private final PostQueryRepository postQueryRepository = mock(PostQueryRepository.class);
    private final MemberQueryRepository memberQueryRepository = mock(MemberQueryRepository.class);
    private final PostLikeQueryRepository postLikeQueryRepository = mock(PostLikeQueryRepository.class);
    private final PostQueryService postQueryService = new PostQueryService(
            postQueryRepository,
            memberQueryRepository,
            postLikeQueryRepository
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(member);
    private final Pageable pageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        given(memberQueryRepository.getById(member.getId())).willReturn(member);
        given(memberQueryRepository.getById(other.getId())).willReturn(other);
        given(memberQueryRepository.getMemberIfIdNotNull(member.getId())).willReturn(member);
        given(memberQueryRepository.getMemberIfIdNotNull(other.getId())).willReturn(other);
        given(memberQueryRepository.getMemberIfIdNotNull(null)).willReturn(null);
    }

    @Nested
    class 포스트_단일_조회_시 {

        @Test
        void 포스트를_조회한다() {
            // given
            Post post = publicPost(1L, blog);
            post.clickLike();
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(
                    1L,
                    blog.getName(),
                    null,
                    null
            );

            // then
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("createdDate")
                    .isEqualTo(PostDetailResponse.builder()
                            .postId(1L)
                            .title("title")
                            .bodyText("content")
                            .visibility(PUBLIC)
                            .writer(new PostDetailResponse.WriterResponse(
                                    member.getId(),
                                    member.getNickname(),
                                    member.getProfileImageUrl()
                            ))
                            .likeCount(1)
                            .isLiked(false)
                            .build());
        }

        @Test
        void 좋아요_눌렀는지_여부가_반영된다() {
            // given
            Post post = publicPost(1L, blog);
            post.clickLike();
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);
            given(postLikeQueryRepository.existsByMemberAndPost(member, post)).willReturn(true);

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(
                    1L,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // then
            assertThat(response.isLiked()).isTrue();
        }

        @Test
        void 블로그_주인은_비공개_글을_볼_수_있다() {
            // given
            Post post = privatePost(1L, blog);
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(
                    1L,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // then
            assertThat(response.title()).isEqualTo(post.getTitle());
        }

        @Test
        void 블로그_주인이_아니라면_비공개_글_조회시_예외() {
            // given
            Post post = privatePost(1L, blog);
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);

            // when & then
            assertThatThrownBy(() ->
                    postQueryService.getByIdAndBlogName(
                            1L,
                            blog.getName(),
                            null,
                            null
                    )
            ).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 블로그_주인이_아닌_경우_비밀번호가_일치하면_보호글을_볼_수_있다() {
            // given
            Post post = protectedPost(1L, blog, "1234");
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(
                    1L,
                    blog.getName(),
                    null,
                    "1234"
            );

            // then
            assertThat(response.title()).isEqualTo(post.getTitle());
            assertThat(response.bodyText()).isEqualTo(post.getBodyText());
            assertThat(response.postThumbnailImageName()).isEqualTo(post.getPostThumbnailImageName());
            assertThat(response.isProtected()).isFalse();
        }

        @Test
        void 블로그_주인이_아니며_비밀번호가_일치하지_않는_경우_보호글_조회시_내용이_보호된다() {
            // given
            Post post = protectedPost(1L, blog, "1234");
            given(postQueryRepository.getById(1L, blog.getName())).willReturn(post);

            // when
            PostDetailResponse response = postQueryService.getByIdAndBlogName(
                    1L,
                    blog.getName(),
                    null,
                    null
            );

            // then
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .ignoringFields("createdDate", "blogId")
                    .isEqualTo(PostDetailResponse.builder()
                            .postId(1L)
                            .title(post.getTitle())
                            .bodyText("보호되어 있는 글입니다. 내용을 보시려면 비밀번호를 입력하세요.")
                            .postThumbnailImageName("")
                            .visibility(PROTECTED)
                            .isProtected(true)
                            .writer(new PostDetailResponse.WriterResponse(
                                    member.getId(),
                                    member.getNickname(),
                                    member.getProfileImageUrl()
                            ))
                            .build());
        }
    }

    @Nested
    class 포스트_검색_시 {

        @Test
        void 포스트를_조건에_맞게_조회한다() {
            // given
            Post post1 = publicPost(1L, blog);
            Post post2 = protectedPost(2L, blog, "1234");
            Post post3 = privatePost(3L, blog);
            PostSearchCond cond = PostSearchCond.builder().build();
            List<Post> content = List.of(post3, post2, post1);
            PageImpl<Post> result = new PageImpl<>(content, pageable, 3);
            given(postQueryRepository.search(member.getId(), cond, pageable))
                    .willReturn(result);

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, member.getId())
                    .getContent();

            // then
            assertThat(responses).hasSize(3)
                    .extracting(PostSearchResponse::id)
                    .containsExactly(3L, 2L, 1L);
        }

        @Test
        void 포스트를_볼_권한이_없는_경우_내용이_보호되어_조회된다() {
            // given
            Post post = protectedPost(2L, blog, "1234");
            PostSearchCond cond = PostSearchCond.builder().build();
            List<Post> content = List.of(post);
            PageImpl<Post> result = new PageImpl<>(content, pageable, 1);
            given(postQueryRepository.search(null, cond, pageable))
                    .willReturn(result);

            // when
            List<PostSearchResponse> responses = postQueryService.search(cond, pageable, null)
                    .getContent();

            // then
            assertThat(responses).hasSize(1)
                    .containsExactly(new PostSearchResponse(
                            2L,
                            blog.getName(),
                            post.getTitle(),
                            "보호되어 있는 글입니다.", "보호되어 있는 글입니다.",
                            "",
                            PROTECTED,
                            0,
                            post.getCreatedDate(),
                            new WriterResponse(member.getId(), member.getNickname(), member.getProfileImageUrl()),
                            new CategoryResponse(null, null),
                            new TagResponses(Collections.emptyList())
                    ));
        }
    }
}
