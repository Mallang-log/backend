package com.mallang.comment.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.comment.CommentFixture.authComment;
import static com.mallang.post.PostFixture.protectedPost;
import static com.mallang.post.PostFixture.publicPost;
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
import com.mallang.comment.application.command.DeleteAuthCommentCommand;
import com.mallang.comment.application.command.UpdateAuthCommentCommand;
import com.mallang.comment.application.command.WriteAuthCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostRepository;
import com.mallang.post.exception.NoAuthorityPostException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("인증된 댓글 작성 서비스 (AuthCommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthCommentServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentDeleteService commentDeleteService = mock(CommentDeleteService.class);
    private final AuthCommentService authCommentService = new AuthCommentService(
            postRepository,
            memberRepository,
            commentRepository,
            commentDeleteService
    );

    private final Member mallang = 깃허브_말랑(1L);
    private final Member donghun = 깃허브_동훈(2L);
    private final Member other = 깃허브_회원(3L, "other");
    private final Blog mallangBlog = mallangBlog(mallang);
    private final Post mallangPublicPost = publicPost(1L, mallangBlog);
    private final Post mallangProtectedPost = protectedPost(2L, mallangBlog, "1234");

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(mallang.getId())).willReturn(mallang);
        given(memberRepository.getById(donghun.getId())).willReturn(donghun);
        given(memberRepository.getById(other.getId())).willReturn(other);
        PostId publicId = mallangPublicPost.getId();
        PostId protectedId = mallangProtectedPost.getId();
        given(postRepository.getById(publicId.getPostId(), mallangBlog.getName()))
                .willReturn(mallangPublicPost);
        given(postRepository.getById(protectedId.getPostId(), mallangBlog.getName()))
                .willReturn(mallangProtectedPost);
    }

    @Nested
    class 댓글_작성_시 {

        @Test
        void 댓글을_작성한다() {
            // given
            var command = new WriteAuthCommentCommand(
                    mallangPublicPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    true,
                    other.getId(),
                    null,
                    null
            );
            given(commentRepository.save(any()))
                    .willReturn(authComment(
                            1L,
                            mallangPublicPost,
                            null,
                            mallang
                    ));

            // when
            Long id = authCommentService.write(command);

            // then
            then(commentRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 대댓글을_작성한다() {
            // given
            AuthComment parent = authComment(
                    1L,
                    mallangPublicPost,
                    null,
                    mallang
            );
            given(commentRepository.getParentByIdAndPost(parent.getId(), mallangPublicPost))
                    .willReturn(parent);
            var command = new WriteAuthCommentCommand(
                    mallangPublicPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    true,
                    other.getId(),
                    parent.getId(),
                    null
            );
            given(commentRepository.save(any())).willReturn(mock(AuthComment.class));

            // when
            Long id = authCommentService.write(command);

            // then
            assertThat(parent.getChildren()).hasSize(1);
            then(commentRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new WriteAuthCommentCommand(
                    mallangProtectedPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    true,
                    other.getId(),
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    authCommentService.write(command)
            ).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 댓글_수정_시 {

        private final AuthComment comment = authComment(
                1L,
                mallangProtectedPost,
                null,
                donghun
        );

        @BeforeEach
        void setUp() {
            given(commentRepository.getAuthCommentById(comment.getId()))
                    .willReturn(comment);
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new UpdateAuthCommentCommand(
                    donghun.getId(),
                    comment.getId(),
                    "수정",
                    true,
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                authCommentService.update(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 댓글을_수정할_권한이_없으면_예외() {
            // given
            var command = new UpdateAuthCommentCommand(
                    mallang.getId(),
                    comment.getId(),
                    "수정",
                    true,
                    "1234"
            );

            // when & then
            assertThatThrownBy(() -> {
                authCommentService.update(command);
            }).isInstanceOf(NoAuthorityCommentException.class);
        }

        @Test
        void 댓글을_수정한다() {
            // given
            var command = new UpdateAuthCommentCommand(
                    donghun.getId(),
                    comment.getId(),
                    "수정",
                    true,
                    "1234"
            );

            // when
            authCommentService.update(command);

            // then
            assertThat(command.content()).isEqualTo("수정");
        }
    }

    @Nested
    class 댓글_삭제_시 {

        private final AuthComment comment = authComment(
                1L,
                mallangProtectedPost,
                null,
                donghun
        );

        @BeforeEach
        void setUp() {
            given(commentRepository.getAuthCommentById(comment.getId()))
                    .willReturn(comment);
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new DeleteAuthCommentCommand(
                    donghun.getId(),
                    comment.getId(),
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                authCommentService.delete(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 댓글을_제거할_권한이_없으면_예외() {
            // given
            var command = new DeleteAuthCommentCommand(
                    other.getId(),
                    comment.getId(),
                    "1234"
            );

            // when & then
            assertThatThrownBy(() -> {
                authCommentService.delete(command);
            }).isInstanceOf(NoAuthorityCommentException.class);
        }

        @Test
        void 댓글을_삭제한다() {
            // given
            var command = new DeleteAuthCommentCommand(
                    donghun.getId(),
                    comment.getId(),
                    "1234"
            );

            // when
            authCommentService.delete(command);

            // then
            then(commentDeleteService)
                    .should(times(1))
                    .delete(comment);
        }
    }
}

