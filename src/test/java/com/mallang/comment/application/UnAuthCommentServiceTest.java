package com.mallang.comment.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static com.mallang.comment.CommentFixture.authComment;
import static com.mallang.comment.CommentFixture.unAuthComment;
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
import com.mallang.comment.application.command.DeleteUnAuthCommentCommand;
import com.mallang.comment.application.command.UpdateUnAuthCommentCommand;
import com.mallang.comment.application.command.WriteUnAuthCommentCommand;
import com.mallang.comment.domain.AuthComment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.UnAuthComment;
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

@DisplayName("비인증 댓글 서비스 (UnAuthCommentService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthCommentServiceTest {

    private final PostRepository postRepository = mock(PostRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final CommentDeleteService commentDeleteService = mock(CommentDeleteService.class);
    private final UnAuthCommentService unAuthCommentService = new UnAuthCommentService(
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
        given(memberRepository.getByIdIfIdNotNull(mallang.getId())).willReturn(mallang);
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
            var command = new WriteUnAuthCommentCommand(
                    mallangPublicPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    "말랑",
                    "123",
                    null,
                    null
            );
            given(commentRepository.save(any()))
                    .willReturn(unAuthComment(
                            1L,
                            mallangPublicPost,
                            "말랑",
                            "123",
                            null
                    ));

            // when
            Long id = unAuthCommentService.write(command);

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
            var command = new WriteUnAuthCommentCommand(
                    mallangPublicPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    "말랑",
                    "123",
                    parent.getId(),
                    null
            );
            given(commentRepository.save(any())).willReturn(mock(UnAuthComment.class));

            // when
            Long id = unAuthCommentService.write(command);

            // then
            assertThat(parent.getChildren()).hasSize(1);
            then(commentRepository)
                    .should(times(1))
                    .save(any());
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new WriteUnAuthCommentCommand(
                    mallangProtectedPost.getId().getPostId(),
                    mallangBlog.getName(),
                    "댓글",
                    "말랑",
                    "123",
                    null,
                    null
            );

            // when & then
            assertThatThrownBy(() ->
                    unAuthCommentService.write(command)
            ).isInstanceOf(NoAuthorityPostException.class);
        }
    }

    @Nested
    class 댓글_수정_시 {

        private final UnAuthComment comment = unAuthComment(
                1L,
                mallangProtectedPost,
                "mallang",
                "123",
                null
        );

        @BeforeEach
        void setUp() {
            given(commentRepository.getUnAuthCommentById(comment.getId()))
                    .willReturn(comment);
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new UpdateUnAuthCommentCommand(
                    comment.getId(),
                    "123",
                    "수정",
                    null
            );

            // when & then
            assertThatThrownBy(() -> {
                unAuthCommentService.update(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 댓글을_수정할_권한이_없으면_예외() {
            // given
            var command = new UpdateUnAuthCommentCommand(
                    comment.getId(),
                    "wrong",
                    "수정",
                    "1234"
            );

            // when & then
            assertThatThrownBy(() -> {
                unAuthCommentService.update(command);
            }).isInstanceOf(NoAuthorityCommentException.class);
        }

        @Test
        void 댓글을_수정한다() {
            // given
            var command = new UpdateUnAuthCommentCommand(
                    comment.getId(),
                    "123",
                    "수정",
                    "1234"
            );

            // when
            unAuthCommentService.update(command);

            // then
            assertThat(command.content()).isEqualTo("수정");
        }
    }

    @Nested
    class 댓글_삭제_시 {

        private final UnAuthComment comment = unAuthComment(
                1L,
                mallangProtectedPost,
                "mallang",
                "123",
                null
        );

        @BeforeEach
        void setUp() {
            given(commentRepository.getUnAuthCommentById(comment.getId()))
                    .willReturn(comment);
        }

        @Test
        void 포스트_접근_불가시_예외() {
            // given
            var command = new DeleteUnAuthCommentCommand(
                    comment.getId(),
                    "123",
                    null,
                    "123"
            );

            // when & then
            assertThatThrownBy(() -> {
                unAuthCommentService.delete(command);
            }).isInstanceOf(NoAuthorityPostException.class);
        }

        @Test
        void 댓글을_제거할_권한이_없으면_예외() {
            // given
            var command = new DeleteUnAuthCommentCommand(
                    comment.getId(),
                    "1234",
                    null,
                    "1234"
            );

            // when & then
            assertThatThrownBy(() -> {
                unAuthCommentService.delete(command);
            }).isInstanceOf(NoAuthorityCommentException.class);
        }

        @Test
        void 댓글을_삭제한다() {
            // given
            var command = new DeleteUnAuthCommentCommand(
                    comment.getId(),
                    "123",
                    null,
                    "1234"
            );

            // when
            unAuthCommentService.delete(command);

            // then
            then(commentDeleteService)
                    .should(times(1))
                    .delete(comment);
        }

        @Test
        void 포스트_작성자가_댓글을_삭제한다() {
            // given
            var command = new DeleteUnAuthCommentCommand(
                    comment.getId(),
                    null,
                    mallang.getId(),
                    null
            );

            // when
            unAuthCommentService.delete(command);

            // then
            then(commentDeleteService)
                    .should(times(1))
                    .delete(comment);
        }
    }
}
