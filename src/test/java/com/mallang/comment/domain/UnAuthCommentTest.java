package com.mallang.comment.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_회원;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.comment.exception.NoAuthorityCommentException;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NoAuthorityPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("익명 사용자의 댓글 (UnAuthComment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthCommentTest {

    private final Member postWriter = 깃허브_회원(100L, "글 작성자");
    private final Blog blog = new Blog("blog", postWriter);
    private final Post post = Post.builder()
            .blog(blog)
            .visibility(PUBLIC)
            .password(null)
            .intro("intro")
            .writer(postWriter)
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
                    .blog(blog)
                    .visibility(PROTECTED)
                    .password("1234")
                    .intro("intro")
                    .writer(postWriter)
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
                }).isInstanceOf(NoAuthorityPostException.class);
            }
        }

        @Nested
        class 비공개_포스트에_작성하는_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .visibility(PRIVATE)
                    .password(null)
                    .writer(postWriter)
                    .intro("intro")
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
                }).isInstanceOf(NoAuthorityPostException.class);
            }
        }
    }


    @Nested
    class 수정_시 {

        @Test
        void 비밀번호가_일치해야_수정_가능하다() {
            // given
            UnAuthComment comment = UnAuthComment.builder()
                    .content("내용")
                    .post(post)
                    .nickname("말랑")
                    .password("1234")
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                comment.validateUpdate("1234", null);
            });
            assertThatThrownBy(() -> {
                comment.validateUpdate("123", null);
            }).isInstanceOf(NoAuthorityCommentException.class);
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
            comment.update("변경");

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
        }

        @Nested
        class 보호_포스트의_댓글을_수정하는_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .visibility(PROTECTED)
                    .password("1234")
                    .intro("intro")
                    .writer(postWriter)
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
                    comment.validateUpdate("comment password", "1234");
                });
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
                    comment.validateUpdate("comment password", "123");
                }).isInstanceOf(NoAuthorityPostException.class);
            }
        }

        @Nested
        class 비공개_포스트의_댓글을_수정하는_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .visibility(PRIVATE)
                    .password(null)
                    .writer(postWriter)
                    .intro("intro")
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
                    comment.validateUpdate("update", null);
                }).isInstanceOf(NoAuthorityPostException.class);
            }
        }
    }

    @Nested
    class 삭제_권한_확인시 {

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
                    comment.validateDelete(null, "1234", null)
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
                    comment.validateDelete(null, "wrong", null)
            ).isInstanceOf(NoAuthorityCommentException.class);
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
                unAuth.validateDelete(postWriter, null, null);
            });
        }

        @Nested
        class 보호_포스트의_댓글을_삭제하는_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .visibility(PROTECTED)
                    .password("1234")
                    .intro("intro")
                    .writer(postWriter)
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
                        comment.validateDelete(null, "comment password", "1234")
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
                        comment.validateDelete(postWriter, null, null)
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
                        comment.validateDelete(null, "comment password", "12")
                ).isInstanceOf(NoAuthorityPostException.class);
            }
        }

        @Nested
        class 비공개_포스트의_댓글을_삭제하는_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .visibility(PROTECTED)
                    .password("1234")
                    .writer(postWriter)
                    .intro("intro")
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
                        comment.validateDelete(postWriter, null, null)
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
                        comment.validateDelete(null, "comment password", null)
                ).isInstanceOf(NoAuthorityPostException.class);
            }
        }
    }
}
