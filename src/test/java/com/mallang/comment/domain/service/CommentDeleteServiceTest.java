package com.mallang.comment.domain.service;

import static com.mallang.member.MemberFixture.memberBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.comment.domain.Comment;
import com.mallang.comment.domain.CommentRepository;
import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterRepository;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("댓글 제거 도메인 서비스(CommentDeleteService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentDeleteServiceTest {

    private final CommentRepository commentRepository =
            mock(CommentRepository.class);
    private final UnAuthenticatedWriterRepository unAuthenticatedWriterRepository =
            mock(UnAuthenticatedWriterRepository.class);
    private final CommentDeleteService commentDeleteService =
            new CommentDeleteService(commentRepository, unAuthenticatedWriterRepository);

    private final Member postWriter = memberBuilder().id(1L).build();
    private final Post post = Post.builder().member(postWriter).build();

    @Test
    void 자신의_댓글이_아닌_경우_예외() {
        // given
        Member member = memberBuilder().id(1L).build();
        CommentWriter writer = new AuthenticatedWriter(member);
        Comment comment = Comment.builder()
                .content("내용")
                .post(post)
                .secret(false)
                .commentWriter(writer)
                .build();

        // when & then
        assertThatThrownBy(() ->
                commentDeleteService.delete(comment, new UnAuthenticatedWriterCredential("123"))
        ).isInstanceOf(NoAuthorityForCommentException.class);
        verify(commentRepository, times(0)).delete(comment);
    }

    @Test
    void 익명_댓글에_대한_비밀번호가_다른_경우_예외() {
        // given
        UnAuthenticatedWriter writer = new UnAuthenticatedWriter("익명", "1234");
        Comment comment = Comment.builder()
                .content("내용")
                .post(post)
                .secret(false)
                .commentWriter(writer)
                .build();

        // when & then
        assertThatThrownBy(() ->
                commentDeleteService.delete(comment, new UnAuthenticatedWriterCredential("123"))
        ).isInstanceOf(NoAuthorityForCommentException.class);
        verify(commentRepository, times(0)).delete(comment);
        verify(unAuthenticatedWriterRepository, times(0)).delete(writer);
    }

    @Test
    void 자신의_댓글인_경우_제거할_수_있다() {
        // given
        UnAuthenticatedWriter writer = new UnAuthenticatedWriter("익명", "1234");
        Comment comment = Comment.builder()
                .content("내용")
                .post(post)
                .secret(false)
                .commentWriter(writer)
                .build();

        // when & then
        assertDoesNotThrow(() ->
                commentDeleteService.delete(comment, new UnAuthenticatedWriterCredential("1234"))
        );
        verify(commentRepository, times(1)).delete(comment);
        verify(unAuthenticatedWriterRepository, times(1)).delete(writer);
    }

    @Test
    void 제거된_댓글의_작성자가_비인증_작성자라면_익명_작성자도_제거된다() {
        // given
        UnAuthenticatedWriter writer = new UnAuthenticatedWriter("익명", "1234");
        Comment comment = Comment.builder()
                .content("내용")
                .post(post)
                .secret(false)
                .commentWriter(writer)
                .build();

        // when & then
        assertDoesNotThrow(() ->
                commentDeleteService.delete(comment, new UnAuthenticatedWriterCredential("1234"))
        );
        verify(commentRepository, times(1)).delete(comment);
        verify(unAuthenticatedWriterRepository, times(1)).delete(writer);
    }

    @Test
    void 포스트_작성자는_모든_댓글_삭제_가능하다() {
        // given
        UnAuthenticatedWriter writer = new UnAuthenticatedWriter("익명", "1234");
        Comment comment = Comment.builder()
                .content("내용")
                .post(post)
                .secret(false)
                .commentWriter(writer)
                .build();

        // when & then
        assertDoesNotThrow(() ->
                commentDeleteService.delete(comment, new AuthenticatedWriterCredential(postWriter.getId()))
        );
        verify(commentRepository, times(1)).delete(comment);
        verify(unAuthenticatedWriterRepository, times(1)).delete(writer);
    }

    @Test
    void 대댓글을_삭제하는_경우_부모와의_연관관계는_끊어지며_물리적으로_제거된다() {
        // given
        Comment parentComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .build();
        Comment childComment = Comment.builder()
                .content("to be delete")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .parent(parentComment)
                .build();
        Comment childComment2 = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .parent(parentComment)
                .build();

        // when
        commentDeleteService.delete(childComment, new AuthenticatedWriterCredential(postWriter.getId()));

        // then
        assertThat(childComment.isDeleted()).isTrue();
        assertThat(childComment.getParent()).isNull();
        assertThat(parentComment.getChildren()).hasSize(1);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment2);
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }

    @Test
    void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
        // given
        UnAuthenticatedWriter mallang = new UnAuthenticatedWriter("mallang", "1");
        UnAuthenticatedWriter donghun = new UnAuthenticatedWriter("donghun", "1");
        Comment parentComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(mallang)
                .secret(false)
                .build();
        Comment childComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(donghun)
                .secret(false)
                .parent(parentComment)
                .build();
        parentComment.delete(new AuthenticatedWriterCredential(postWriter.getId()));

        // when
        commentDeleteService.delete(childComment, new AuthenticatedWriterCredential(postWriter.getId()));

        // then
        assertThat(childComment.isDeleted()).isTrue();
        assertThat(childComment.getParent()).isNull();
        assertThat(parentComment.getChildren()).isEmpty();
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(1)).delete(parentComment);
        verify(unAuthenticatedWriterRepository, times(1)).delete(mallang);
        verify(unAuthenticatedWriterRepository, times(1)).delete(donghun);
    }

    @Test
    void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태가_아닌_경우_부모는_변함없다() {
        // given
        Comment parentComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .build();
        Comment childComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .parent(parentComment)
                .build();

        // when
        commentDeleteService.delete(childComment, new AuthenticatedWriterCredential(postWriter.getId()));

        // then
        assertThat(childComment.isDeleted()).isTrue();
        assertThat(childComment.getParent()).isNull();
        assertThat(parentComment.getChildren()).isEmpty();
        verify(commentRepository, times(1)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }

    @Test
    void 자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
        // given
        Comment parentComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .build();
        Comment childComment = Comment.builder()
                .content("내용")
                .post(post)
                .commentWriter(new AuthenticatedWriter(postWriter))
                .secret(true)
                .parent(parentComment)
                .build();

        // when
        parentComment.delete(new AuthenticatedWriterCredential(postWriter.getId()));

        // then
        assertThat(parentComment.isDeleted()).isTrue();
        assertThat(childComment.getParent()).isEqualTo(parentComment);
        assertThat(parentComment.getChildren()).hasSize(1);
        assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
        verify(commentRepository, times(0)).delete(childComment);
        verify(commentRepository, times(0)).delete(parentComment);
    }
}
