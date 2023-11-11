package com.mallang.comment.domain;

import static com.mallang.member.MemberFixture.회원;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.member.domain.Member;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("익명 사용자의 댓글(UnAuthenticatedComment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthenticatedCommentTest {

    private final Member postWriter = 회원(100L, "글 작성자");
    private final Blog blog = new Blog("blog", postWriter);
    private final Post post = Post.builder()
            .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
            .writer(postWriter)
            .blog(blog)
            .build();

    @Nested
    class 작성_시 {

        @Test
        void 공개_댓글만_작성_가능하다() {
            // when
            UnAuthenticatedComment unAuth = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // then
            assertThat(unAuth.getContent()).isEqualTo("내용");
            assertThat(unAuth.getPassword()).isEqualTo("1234");
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 비밀번호가_다른_경우_예외() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update("12345", "말랑", null)
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
            comment.update("1234", "변경", null);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
        }
    }

    @Nested
    class 삭제_시 {

        private final CommentRepository commentRepository = mock(CommentRepository.class);
        private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

        @Test
        void 비밀번호가_일치하면_제거할_수_있다() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(null, "1234", commentDeleteService, null)
            );
        }

        @Test
        void 비밀번호가_다른_경우_예외() {
            // given
            UnAuthenticatedComment comment = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(null, "12345", commentDeleteService, null)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 포스트_작성자는_모든_댓글_삭제_가능하다() {
            // given
            UnAuthenticatedComment unAuth = UnAuthenticatedComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                unAuth.delete(postWriter, null, commentDeleteService, null);
            });
        }
    }
}
