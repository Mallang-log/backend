package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.blog;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.post.DraftFixture.draft;
import static com.mallang.post.PostCategoryFixture.postCategory;
import static com.mallang.post.PostFixture.publicPost;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.post.application.command.CreatePostCommand;
import com.mallang.post.application.command.DeletePostCommand;
import com.mallang.post.application.command.UpdatePostCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategory;
import com.mallang.post.domain.PostCategoryRepository;
import com.mallang.post.domain.PostIdGenerator;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.draft.Draft;
import com.mallang.post.domain.draft.DraftRepository;
import com.mallang.post.exception.NoAuthorityDraftException;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import com.mallang.post.exception.NoAuthorityPostException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@DisplayName("포스트 서비스 (PostService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final DraftRepository draftRepository = mock(DraftRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostCategoryRepository postCategoryRepository = mock(PostCategoryRepository.class);
    private final PostIdGenerator postIdGenerator = mock(PostIdGenerator.class);
    private final PostService postService = new PostService(
            blogRepository,
            postRepository,
            draftRepository,
            memberRepository,
            postCategoryRepository,
            postIdGenerator
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(1L, member);
    private final Blog otherBlog = blog(2L, other);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(blogRepository.getByName(blog.getName())).willReturn(blog);
        given(blogRepository.getByName(otherBlog.getName())).willReturn(otherBlog);
        given(postRepository.save(any())).willReturn(mock(Post.class));
    }

    @Nested
    class 포스트_저장_시 {

        @Test
        void 포스트를_저장한다() {
            // given
            var command = new CreatePostCommand(
                    member.getId(),
                    blog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    null,
                    of()
            );

            // when
            postService.create(command);

            // then
            then(postRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 다른_사람의_블로그에_대한_포스트를_작성시_예외() {
            // given
            var command = new CreatePostCommand(
                    other.getId(),
                    blog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    null,
                    of()
            );

            // when & then
            assertThatThrownBy(() -> {
                postService.create(command);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 다른_사람의_카테고리라면_예외() {
            // given
            PostCategory postCategory = postCategory(1L, "spring", otherBlog);
            given(postCategoryRepository.getByIdIfIdNotNull(postCategory.getId()))
                    .willReturn(postCategory);
            var command = new CreatePostCommand(
                    member.getId(),
                    blog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    postCategory.getId(),
                    of()
            );

            // when & then
            assertThatThrownBy(() ->
                    postService.create(command)
            ).isInstanceOf(NoAuthorityPostCategoryException.class);
        }
    }

    @Nested
    class 임시_글로부터_포스트_생성_시 {

        @Test
        void 포스트는_생성되고_임시_글은_제거된다() {
            // given
            Draft draft = draft(1L, blog);
            given(draftRepository.getById(draft.getId())).willReturn(draft);
            given(postRepository.save(any()))
                    .willReturn(mock(Post.class));
            var command = new CreatePostCommand(
                    member.getId(),
                    blog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    null,
                    of()
            );

            // when
            postService.createFromDraft(command, draft.getId());

            // then
            then(postRepository)
                    .should(times(1))
                    .save(any());
            then(draftRepository)
                    .should(times(1))
                    .delete(draft);
        }

        @Test
        void 다른_블로그의_임시_글을_통해_생성하려는_경우_예외() {
            // given
            Draft draft = draft(1L, blog(2L, other));
            given(draftRepository.getById(draft.getId())).willReturn(draft);
            given(postRepository.save(any()))
                    .willReturn(mock(Post.class));
            var command = new CreatePostCommand(
                    member.getId(),
                    blog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    null,
                    of()
            );

            // when & then
            assertThatThrownBy(() ->
                    postService.createFromDraft(command, draft.getId())
            ).isInstanceOf(NoAuthorityDraftException.class);
        }

        @Test
        void 다른_사람의_블로그에_작성하는_경우_예외() {
            // given
            Draft draft = draft(1L, blog);
            given(draftRepository.getById(draft.getId())).willReturn(draft);
            given(postRepository.save(any()))
                    .willReturn(mock(Post.class));
            var command = new CreatePostCommand(
                    member.getId(),
                    otherBlog.getName(),
                    "title",
                    "intro",
                    "text",
                    null,
                    PUBLIC,
                    null,
                    null,
                    of()
            );

            // when & then
            assertThatThrownBy(() ->
                    postService.createFromDraft(command, draft.getId())
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Nested
    class 포스트_수정_시 {

        private final Long postId = 10L;
        private final Post post = publicPost(postId, blog);

        @BeforeEach
        void setUp() {
            given(postRepository.getById(postId, blog.getName())).willReturn(post);
        }

        @Test
        void 포스트를_수정한다() {
            // given
            var command = new UpdatePostCommand(
                    member.getId(),
                    postId,
                    blog.getName(),
                    "수정제목",
                    "수정인트로",
                    "수정내용",
                    "수정썸네일",
                    PUBLIC,
                    null,
                    null,
                    of("태그2")
            );

            // when
            postService.update(command);

            // then
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getBodyText()).isEqualTo("수정내용");
            assertThat(post.getPostThumbnailImageName()).isEqualTo("수정썸네일");
            assertThat(post.getPostIntro()).isEqualTo("수정인트로");
            assertThat(post.getTags()).hasSize(1);
        }

        @Test
        void 다른_사람의_포스트는_수정할_수_없다() {
            // given
            var command = new UpdatePostCommand(
                    other.getId(),
                    postId,
                    blog.getName(),
                    "수정제목",
                    "수정인트로",
                    "수정내용",
                    "수정썸네일",
                    PUBLIC,
                    null,
                    null,
                    of("태그2")
            );

            // when & then
            assertThatThrownBy(() -> {
                postService.update(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 포스트_제거_시 {

        private final Long post1Id = 10L;
        private final Post post1 = publicPost(post1Id, blog);
        private final Long post2Id = 12L;
        private final Post post2 = publicPost(post2Id, blog);

        @Test
        void 타인의_글이_포함된_경우_예외() {
            // given
            given(postRepository.findAllByIdIn(of(post1Id, post2Id), blog.getName()))
                    .willReturn(List.of(post1, post2));
            var command = new DeletePostCommand(
                    other.getId(),
                    of(post1Id, post2Id),
                    blog.getName()
            );

            // when & then
            assertThatThrownBy(() -> {
                postService.delete(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 원하는_포스트들을_제거하며_각각_포스트_제거_이벤트가_발행된다() {
            // when
            given(postRepository.findAllByIdIn(of(10L, 12L), blog.getName()))
                    .willReturn(List.of(post1, post2));
            var command = new DeletePostCommand(
                    member.getId(),
                    of(post1Id, post2Id),
                    blog.getName()
            );

            // when
            postService.delete(command);

            // then
            assertThat(post1.domainEvents()).hasSize(1);
            assertThat(post2.domainEvents()).hasSize(1);
            then(postRepository)
                    .should(times(1))
                    .delete(post1);
            then(postRepository)
                    .should(times(1))
                    .delete(post2);
        }
    }
}
