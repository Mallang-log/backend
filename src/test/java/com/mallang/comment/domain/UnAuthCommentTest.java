package com.mallang.comment.domain;

import static com.mallang.auth.MemberFixture.회원;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.domain.service.CommentDeleteService;
import com.mallang.comment.exception.NoAuthorityForCommentException;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("익명 사용자의 댓글(UnAuthComment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthCommentTest {

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
            UnAuthComment unAuth = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // then
            assertThat(unAuth.getContent()).isEqualTo("내용");
            assertThat(unAuth.getPassword()).isEqualTo("1234");
        }

        @Nested
        class 공개_포스트에_작성하는_경우 {

            @Test
            void 누구나_작성_가능하다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("1234")
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.write(null);
                });
                assertThat(comment.getContent()).isEqualTo("내용");
            }
        }

        @Nested
        class 보호_포스트에_작성하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 포스트의_비밀번호가_일치하면_작성할_수_있다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("1234")
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.write("1234");
                });
                assertThat(comment.content).isEqualTo("내용");
            }

            @Test
            void 포스트의_비밀번호가_일치하지_않으면_작성할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("1234")
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.write("123");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }

        @Nested
        class 비공개_포스트에_작성하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                    .blog(blog)
                    .build();

            @Test
            void 아무도_작성할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("1234")
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.write(null);
                }).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 비밀번호가_다른_경우_예외() {
            // given
            UnAuthComment comment = UnAuthComment.builder()
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
            UnAuthComment comment = UnAuthComment.builder()
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

        @Nested
        class 보호_포스트의_댓글을_수정하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 댓글_비밀번호가_일치하고_포스트의_비밀번호가_일치하면_수정할_수_있다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.update("comment password", "update", "1234");
                });
                assertThat(comment.getContent()).isEqualTo("update");
            }

            @Test
            void 포스트의_비밀번호가_일치하지_않으면_수정할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.update("comment password", "update", "12");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(comment.getContent()).isEqualTo("내용");
            }
        }

        @Nested
        class 비공개_포스트의_댓글을_수정하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                    .blog(blog)
                    .build();

            @Test
            void 수정할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.update("comment password", "update", null);
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(comment.getContent()).isEqualTo("내용");
            }
        }
    }

    @Nested
    class 삭제_시 {

        private final CommentRepository commentRepository = mock(CommentRepository.class);
        private final CommentDeleteService commentDeleteService = new CommentDeleteService(commentRepository);

        @Test
        void 비밀번호가_일치하면_제거할_수_있다() {
            // given
            UnAuthComment comment = UnAuthComment.builder()
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
            UnAuthComment comment = UnAuthComment.builder()
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
            UnAuthComment unAuth = UnAuthComment.builder()
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

        @Nested
        class 보호_포스트의_댓글을_삭제하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 댓글_비밀번호가_일치하고_포스트의_비밀번호가_일치하면_삭제할_수_있다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(null, "comment password", commentDeleteService, "1234")
                );
            }

            @Test
            void 포스트_작성자라면_삭제할_수_있다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(postWriter, null, commentDeleteService, null)
                );
            }

            @Test
            void 포스트_작성자가_아니며_포스트의_비밀번호도_일치하지_않으면_삭제할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertThatThrownBy(() ->
                        comment.delete(null, "comment password", commentDeleteService, "12")
                ).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }

        @Nested
        class 비공개_포스트의_댓글을_삭제하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                    .blog(blog)
                    .build();

            @Test
            void 포스트_작성자만_삭제할_수_있다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(postWriter, null, commentDeleteService, null)
                );
            }

            @Test
            void 포스트_작성자가_아니면_삭제할_수_없다() {
                // given
                UnAuthComment comment = UnAuthComment.builder()
                        .content("내용")
                        .post(post)
                        .nickname("말랑")
                        .password("comment password")
                        .build();

                // when & then
                assertThatThrownBy(() ->
                        comment.delete(null, "comment password", commentDeleteService, null)
                ).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }
    }
}
