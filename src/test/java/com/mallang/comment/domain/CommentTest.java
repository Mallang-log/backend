package com.mallang.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.comment.exception.CannotWriteSecretCommentException;
import com.mallang.comment.exception.NoAuthorityForCommentException;
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

    private final Post post = Post.builder().build();

    @Nested
    class 생성_시 {

        @Test
        void 비밀_댓글은_로그인한_사용자만_작성_가능하다() {
            // when
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(1L))
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
                            .commentWriter(new AnonymousWriter("익명", "1234"))
                            .secret(true)
                            .build()
            ).isInstanceOf(CannotWriteSecretCommentException.class);
        }

        @Test
        void 공개_댓글은_로그인한_사용자와_익명_사용자_모두_작성_가능하다() {
            // when
            Comment commentFromAuthenticated = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AuthenticatedWriter(1L))
                    .secret(false)
                    .build();
            Comment commentFromAnonymous = Comment.builder()
                    .content("내용")
                    .post(post)
                    .commentWriter(new AnonymousWriter("익명", "1234"))
                    .secret(false)
                    .build();

            // then
            assertThat(commentFromAuthenticated.isSecret()).isFalse();
            assertThat(commentFromAnonymous.isSecret()).isFalse();
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자신의_댓글이_아닌_경우_예외_case_로그인_유저() {
            // given
            CommentWriter writer = new AuthenticatedWriter(1L);
            CommentWriter other = new AnonymousWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(other, "변경", false)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외_case_익명_유저() {
            // given
            CommentWriter writer = new AnonymousWriter("익명", "1234");
            CommentWriter other = new AuthenticatedWriter(1L);
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(other, "변경", false)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 익명_유저가_댓글_변경_시_댓글을_비공개로_바꿀_경우_예외() {
            // given
            CommentWriter writer = new AnonymousWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(writer, "변경", true)
            ).isInstanceOf(CannotWriteSecretCommentException.class);
        }

        @Test
        void 댓글을_변경한다() {
            // given
            CommentWriter writer = new AnonymousWriter("익명", "1234");
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .commentWriter(writer)
                    .build();

            // when
            comment.update(writer, "변경", false);

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
            CommentWriter writer = new AuthenticatedWriter(1L);
            Comment comment = Comment.builder()
                    .content("내용")
                    .post(post)
                    .secret(before)
                    .commentWriter(writer)
                    .build();

            // when
            comment.update(writer, "변경", after);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isEqualTo(after);
        }
    }
}
