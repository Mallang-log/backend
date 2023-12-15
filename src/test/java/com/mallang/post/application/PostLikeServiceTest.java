package com.mallang.post.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.post.PostFixture;
import com.mallang.post.application.command.CancelPostLikeCommand;
import com.mallang.post.application.command.ClickPostLikeCommand;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.domain.like.PostLike;
import com.mallang.post.domain.like.PostLikeRepository;
import com.mallang.post.domain.like.PostLikeValidator;
import com.mallang.post.exception.AlreadyLikedPostException;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.NotFoundPostLikeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요 서비스 (PostLikeService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final PostLikeRepository postLikeRepository = mock(PostLikeRepository.class);
    private final PostLikeValidator postLikeValidator = mock(PostLikeValidator.class);
    private final PostLikeService postLikeService = new PostLikeService(
            postRepository,
            memberRepository,
            postLikeRepository,
            postLikeValidator
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(member);
    private final Long postId = 10L;
    private final Post post = PostFixture.publicPost(postId, blog);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(postRepository.getById(postId, blog.getName())).willReturn(post);
    }

    @Nested
    class 좋아요_시 {

        @Test
        void 좋아요를_누를_수_있다() {
            // given
            var command = new ClickPostLikeCommand(
                    postId,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // when
            postLikeService.like(command);

            // then
            then(postLikeRepository)
                    .should(times(1))
                    .save(any());
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 회원이_이미_해당_포스트에_좋아요를_누른_경우_예외() {
            // given
            willThrow(AlreadyLikedPostException.class)
                    .given(postLikeValidator)
                    .validateClickLike(post, member);
            var command = new ClickPostLikeCommand(
                    postId,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.like(command);
            }).isInstanceOf(AlreadyLikedPostException.class);
        }

        @Test
        void 포스트에_대한_접근_권한이_없으면_예외() {
            // given
            Post post = PostFixture.privatePost(postId, blog);
            given(postRepository.getById(postId, blog.getName())).willReturn(post);
            var command = new ClickPostLikeCommand(
                    postId,
                    blog.getName(),
                    other.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.like(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 좋아요_취소_시 {

        @Test
        void 좋아요_취소_시_좋아요가_제거된다() {
            // given
            PostLike postLike = new PostLike(post, member);
            postLike.like(postLikeValidator, null);
            given(postLikeRepository.getByPostAndMember(postId, blog.getName(), member.getId()))
                    .willReturn(postLike);
            var command = new CancelPostLikeCommand(
                    postId,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // when
            postLikeService.cancel(command);

            // then
            then(postLikeRepository)
                    .should(times(1))
                    .delete(postLike);
            assertThat(post.getLikeCount()).isZero();
        }

        @Test
        void 누른_좋아요가_없으면_예외() {
            // given
            willThrow(NotFoundPostLikeException.class)
                    .given(postLikeRepository)
                    .getByPostAndMember(postId, blog.getName(), member.getId());
            var command = new CancelPostLikeCommand(
                    postId,
                    blog.getName(),
                    member.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                postLikeService.cancel(command);
            }).isInstanceOf(NotFoundPostLikeException.class);
        }
    }
}
