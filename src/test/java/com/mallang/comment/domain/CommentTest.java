package com.mallang.comment.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.comment.domain.writer.AuthenticatedWriter;
import com.mallang.comment.domain.writer.AuthenticatedWriterCredential;
import com.mallang.comment.domain.writer.CommentWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import com.mallang.comment.domain.writer.UnAuthenticatedWriterCredential;
import com.mallang.comment.exception.CannotWriteSecretCommentException;
import com.mallang.comment.exception.CommentDepthConstraintViolationException;
import com.mallang.comment.exception.DifferentPostFromParentCommentException;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("댓글(Comment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentTest {

    private final Member postWriter = memberBuilder().id(1L).build();
    private final Post post = Post.builder().member(postWriter).build();

    @Nested
    class 작성_시 {

        @Test
        void 비밀_댓글은_로그인한_사용자만_작성_가능하다() {
            // when
            Member member = memberBuilder().id(1L).build();
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(member))
                    .secret(true)
                    .build();

            // then
            assertThat(comment.isSecret()).isTrue();
        }

        @Test
        void 익명_사용자가_비밀_댓글을_작성하는_경우_오류() {
            // when & then
            assertThatThrownBy(() ->
                    Comment.builder()
                            .content("내용")
                            .post(post)
                            .commentWriter(new UnAuthenticatedWriter("익명", "1234"))
                            .secret(true)
                            .build()
            ).isInstanceOf(CannotWriteSecretCommentException.class);
        }

        @Test
        void 공개_댓글은_로그인한_사용자와_익명_사용자_모두_작성_가능하다() {
            // when
            Member member = memberBuilder().id(1L).build();
            Comment commentFromAuthenticated = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(member))
                    .secret(false)
                    .build();
            Comment commentFromAnonymous = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new UnAuthenticatedWriter("익명", "1234"))
                    .secret(false)
                    .build();

            // then
            assertThat(commentFromAuthenticated.isSecret()).isFalse();
            assertThat(commentFromAnonymous.isSecret()).isFalse();
        }
    }

    @Nested
    class 대댓글_작성_시 {

        @Test
        void 대댓글을_달_수_있다() {
            // given
            Comment parentComment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(postWriter))
                    .secret(true)
                    .build();

            // when
            Comment childComment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(postWriter))
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // then
            assertThat(childComment.getParent()).isEqualTo(parentComment);
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
        }

        @Test
        void 다른_사람의_댓글에도_대댓글을_달_수_있다() {
            // given
            Comment parentComment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(postWriter))
                    .secret(true)
                    .build();
            Member other = memberBuilder().id(2L).build();

            // when
            Comment childComment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(other))
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // then
            assertThat(childComment.getParent()).isEqualTo(parentComment);
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
        }

        @Test
        void 대댓글에_대해서는_댓글을_달_수_없다() {
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
            assertThatThrownBy(() ->
                    Comment.builder()
                            .content("내용")
                            .post(post)
                            .commentWriter(new AuthenticatedWriter(postWriter))
                            .secret(true)
                            .parent(childComment)
                            .build()
            ).isInstanceOf(CommentDepthConstraintViolationException.class);

            // then
            assertThat(childComment.getChildren()).isEmpty();
        }

        @Test
        void 대댓글을_다는_경우_부모_댓글과_Post_가_다르면_예외이다() {
            // given
            Comment parentComment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(postWriter))
                    .secret(true)
                    .build();
            Post otherPost = Post.builder().member(postWriter).build();

            // when
            assertThatThrownBy(() ->
                    Comment.builder()
                            .content("내용")
                            .post(otherPost)
                            .commentWriter(new AuthenticatedWriter(postWriter))
                            .secret(true)
                            .parent(parentComment)
                            .build()
            ).isInstanceOf(DifferentPostFromParentCommentException.class);

            // then
            assertThat(parentComment.getChildren()).isEmpty();
        }
    }

    @Nested
    class 수정_시 {

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
                    comment.update(new AuthenticatedWriterCredential(2L), "변경", false)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 익명_댓글에_대한_비밀번호가_다른_경우_예외() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(new UnAuthenticatedWriterCredential("123"), "변경", false)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 익명_유저가_댓글_변경_시_댓글을_비공개로_바꿀_경우_예외() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(new UnAuthenticatedWriterCredential("1234"), "변경", true)
            ).isInstanceOf(CannotWriteSecretCommentException.class);
        }

        @Test
        void 댓글을_변경한다() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when
            comment.update(new UnAuthenticatedWriterCredential("1234"), "변경", false);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isFalse();
        }

        @ParameterizedTest(name = "(공개여부({0}) -> 공개여부({1}))")
        @CsvSource(
                value = {
                        "true -> false",
                        "false -> true",
                }, delimiterString = " -> ")
        void 로그인한_유저는_비공개_여부도_변경할_수_있다(boolean before, boolean after) {
            // given
            Member member = memberBuilder().id(1L).build();
            CommentWriter writer = new AuthenticatedWriter(member);
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(before)
                    .commentWriter(writer)
                    .build();

            // when
            comment.update(new AuthenticatedWriterCredential(1L), "변경", after);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isEqualTo(after);
        }
    }

    @Nested
    class 삭제_시 {

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
                    comment.delete(new UnAuthenticatedWriterCredential("123"))
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 익명_댓글에_대한_비밀번호가_다른_경우_예외() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(new UnAuthenticatedWriterCredential("123"))
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 자신의_댓글인_경우_제거할_수_있다() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(new UnAuthenticatedWriterCredential("1234"))
            );
        }

        @Test
        void 포스트_작성자는_모든_댓글_삭제_가능하다() {
            // given
            CommentWriter writer = new UnAuthenticatedWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(new AuthenticatedWriterCredential(postWriter.getId()))
            );
        }

        @Test
        void 대댓글인_경우_부모_댓글과_관계가_끊어진다() {
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
            childComment.delete(new AuthenticatedWriterCredential(postWriter.getId()));

            // then
            assertThat(childComment.isDeleted()).isTrue();
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).isEmpty();
        }

        @Test
        void 자식_댓글이_존재한다면_제거된_상태이나_자식_댓글과_관계는_유지된다() {
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
        }
    }
}
