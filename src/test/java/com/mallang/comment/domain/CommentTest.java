package com.mallang.comment.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static com.mallang.member.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.mallang.comment.domain.service.CommentDeleteService;
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

    private final Member postWriter = 말랑(1L);
    private final Post post = Post.builder()
            .member(postWriter)
            .build();

    @Nested
    class 작성_시 {

        @Test
        void 비밀_댓글은_로그인한_사용자만_작성_가능하다() {
            // when
            Member member = 말랑(1L);

            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();

            // then
            assertThat(comment.isSecret()).isTrue();
        }

        @Test
        void 공개_댓글은_로그인한_사용자와_익명_사용자_모두_작성_가능하다() {
            // when
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment auth = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();
            UnAuthenticatedComment unAuth = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // then
            assertThat(auth.getContent()).isEqualTo("내용");
            assertThat(unAuth.getContent()).isEqualTo("내용");
        }
    }

    @Nested
    class 대댓글_작성_시 {

        @Test
        void 대댓글을_달_수_있다() {
            // given
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .build();

            // when
            AuthenticatedComment child = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .parent(parent)
                    .build();

            // then
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 다른_사람의_댓글에도_대댓글을_달_수_있다() {
            // given
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .build();
            Member other = memberBuilder().id(2L).build();

            // when
            AuthenticatedComment child = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(other)
                    .secret(false)
                    .parent(parent)
                    .build();

            // then
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 대댓글에_대해서는_댓글을_달_수_없다() {
            // given
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .build();
            AuthenticatedComment child = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .parent(parent)
                    .build();

            // when
            assertThatThrownBy(() ->
                    AuthenticatedComment.builder()
                            .content("내용")
                            .post(post)
                            .writer(postWriter)
                            .secret(false)
                            .parent(child)
                            .build()
            ).isInstanceOf(CommentDepthConstraintViolationException.class);

            // then
            assertThat(child.getChildren()).isEmpty();
        }

        @Test
        void 대댓글을_다는_경우_부모_댓글과_Post_가_다르면_예외이다() {
            // given
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(postWriter)
                    .secret(false)
                    .build();
            Post otherPost = Post.builder().member(postWriter).build();

            // when
            assertThatThrownBy(() ->
                    AuthenticatedComment.builder()
                            .content("내용")
                            .post(otherPost)
                            .writer(postWriter)
                            .secret(true)
                            .parent(parent)
                            .build()
            ).isInstanceOf(DifferentPostFromParentCommentException.class);

            // then
            assertThat(parent.getChildren()).isEmpty();
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            Member member = memberBuilder().id(1L).build();
            Member other = memberBuilder().id(2L).build();
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(other, "수정", true)
            ).isInstanceOf(NoAuthorityForCommentException.class);

        }

        @Test
        void 익명_댓글에_대한_비밀번호가_다른_경우_예외() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update("12345", "말랑")
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 댓글을_변경한다() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when
            comment.update("1234", "변경");

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
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
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(before)
                    .writer(member)
                    .build();

            // when
            comment.update(member, "변경", after);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isEqualTo(after);
        }
    }

    @Nested
    class 삭제_시 {

        private final CommentRepository commentRepository = mock(CommentRepository.class);
        private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            Member member = memberBuilder().id(1L).build();
            Member other = memberBuilder().id(2L).build();
            AuthenticatedComment comment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(other, commentDeleteService)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 익명_댓글에_대한_비밀번호가_다른_경우_예외() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(null, "12345", commentDeleteService)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 자신의_댓글인_경우_제거할_수_있다() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(null, "1234", commentDeleteService)
            );
        }

        @Test
        void 포스트_작성자는_모든_댓글_삭제_가능하다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment authComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();
            UnAuthenticatedComment unAuth = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                authComment.delete(postWriter, commentDeleteService);
                unAuth.delete(postWriter, null, commentDeleteService);
            });
        }

        @Test
        void 대댓글인_경우_부모_댓글과_관계가_끊어진다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();
            UnAuthenticatedComment child = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .parent(parent)
                    .build();

            // when
            child.delete(null, "1234", commentDeleteService);

            // then
            assertThat(child.isDeleted()).isTrue();
            assertThat(child.getParent()).isNull();
            assertThat(parent.getChildren()).isEmpty();
        }

        @Test
        void 댓글_제거_시_자식_댓글이_존재한다면_제거된_상태이나_자식_댓글과_관계는_유지된다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parent = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();
            UnAuthenticatedComment child = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .parent(parent)
                    .build();

            // when
            parent.delete(member, commentDeleteService);

            // then
            assertThat(parent.isDeleted()).isTrue();
            assertThat(child.getParent()).isEqualTo(parent);
            assertThat(parent.getChildren()).hasSize(1);
            assertThat(parent.getChildren().get(0)).isEqualTo(child);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모와의_연관관계는_끊어지며_물리적으로_제거된다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parentComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthenticatedComment childComment = AuthenticatedComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();
            AuthenticatedComment childComment2 = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            childComment.delete(member, commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment2);
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태이며_더이상_존재하는_자식이_없는_경우_부모_댓글도_물리적으로_제거된다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parentComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthenticatedComment childComment = AuthenticatedComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();
            parentComment.delete(member, commentDeleteService);

            // when
            childComment.delete(member, commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).isEmpty();
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(1)).delete(parentComment);
        }

        @Test
        void 대댓글을_삭제하는_경우_부모_댓글이_논리적으로_제거된_상태가_아닌_경우_부모는_변함없다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parentComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthenticatedComment childComment = AuthenticatedComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            childComment.delete(member, commentDeleteService);

            // then
            assertThat(childComment.getParent()).isNull();
            assertThat(parentComment.getChildren()).isEmpty();
            verify(commentRepository, times(1)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }

        @Test
        void 자식_댓글이_존재한다면_부모와의_연관관계는_유지되며_논리적으로만_제거시킨다() {
            // given
            Member member = memberBuilder().id(1L).build();
            AuthenticatedComment parentComment = AuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();
            AuthenticatedComment childComment = AuthenticatedComment.builder()
                    .content("to be delete")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .parent(parentComment)
                    .build();

            // when
            parentComment.delete(member, commentDeleteService);

            // then
            assertThat(childComment.getParent()).isEqualTo(parentComment);
            assertThat(parentComment.getChildren()).hasSize(1);
            assertThat(parentComment.getChildren().get(0)).isEqualTo(childComment);
            verify(commentRepository, times(0)).delete(childComment);
            verify(commentRepository, times(0)).delete(parentComment);
        }
    }
}
