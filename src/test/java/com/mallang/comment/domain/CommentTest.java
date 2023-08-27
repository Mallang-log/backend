package com.mallang.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.comment.exception.CannotWriteSecretCommentException;
import com.mallang.post.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("댓글(Comment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CommentTest {

    @Nested
    class 생성_시 {

        private final Post post = Post.builder().build();

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
}
