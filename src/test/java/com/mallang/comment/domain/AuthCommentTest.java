package com.mallang.comment.domain;

import static com.mallang.auth.MemberFixture.동훈;
import static com.mallang.auth.MemberFixture.말랑;
import static com.mallang.auth.MemberFixture.회원;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
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
import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("인증된 사용자의 댓글(AuthComment) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthCommentTest {

    private final Member postWriter = 회원(100L, "글 작성자");
    private final Blog blog = new Blog("blog", postWriter);
    private final Post post = Post.builder()
            .writer(postWriter)
            .visibilityPolish(new PostVisibilityPolicy(Visibility.PUBLIC, null))
            .blog(blog)
            .build();
    private final Member member = 말랑(1L);
    private final Member other = 동훈(2L);

    @Nested
    class 작성_시 {

        @Test
        void 비밀_댓글은_로그인한_사용자만_작성_가능하다() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(true)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                comment.write(null);
            });
            assertThat(comment.isSecret()).isTrue();
            assertThat(comment.getContent()).isEqualTo("내용");
        }

        @Test
        void 공개_댓글을_작성할_수_있다() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .writer(member)
                    .secret(false)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                comment.write(null);
            });
            assertThat(comment.isSecret()).isFalse();
            assertThat(comment.getContent()).isEqualTo("내용");
        }

        @Nested
        class 공개_포스트에_작성하는_경우 {

            @Test
            void 로그인한_누구나_작성_가능하다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(other)
                        .secret(false)
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
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(other)
                        .secret(true)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.write("1234");
                });
                assertThat(comment.content).isEqualTo("내용");
            }

            @Test
            void 포스트_작성자라면_작성할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(postWriter)
                        .secret(false)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.write(null);
                });
                assertThat(comment.content).isEqualTo("내용");
            }

            @Test
            void 포스트_작성자가_아니며_비밀번호도_일치하지_않으면_작성할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(other)
                        .secret(false)
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
            void 포스트_작성자만_작성할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(postWriter)
                        .secret(true)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.write(null);
                });
                assertThat(comment.content).isEqualTo("내용");
            }

            @Test
            void 포스트_작성자가_아니면_작성할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .writer(other)
                        .secret(false)
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
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(postWriter)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.update(other, "수정", true, null)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 댓글을_변경한다() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(true)
                    .writer(member)
                    .build();

            // when
            comment.update(member, "update", true, null);

            // then
            assertThat(comment.getContent()).isEqualTo("update");
        }

        @ParameterizedTest(name = "(공개여부({0}) -> 공개여부({1}))")
        @CsvSource(
                value = {
                        "true -> false",
                        "false -> true",
                }, delimiterString = " -> ")
        void 로그인한_유저는_비공개_여부도_변경할_수_있다(boolean before, boolean after) {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(before)
                    .writer(member)
                    .build();

            // when
            comment.update(member, "변경", after, null);

            // then
            assertThat(comment.getContent()).isEqualTo("변경");
            assertThat(comment.isSecret()).isEqualTo(after);
        }

        @Nested
        class 공개_포스트의_댓글을_수정하는_경우 {

            @Test
            void 댓글_작성자라면_가능하다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(other)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.update(other, "update", true, null);
                });
                assertThat(comment.getContent()).isEqualTo("update");
            }
        }

        @Nested
        class 보호_포스트의_댓글을_수정하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 댓글_작성자이며_포스트의_비밀번호가_일치하면_수정할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(other)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.update(other, "update", true, "1234");
                });
                assertThat(comment.getContent()).isEqualTo("update");
            }

            @Test
            void 댓글_작성자가_포스트_작성자라면_수정할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(postWriter)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.update(postWriter, "update", true, null);
                });
                assertThat(comment.getContent()).isEqualTo("update");
            }

            @Test
            void 댓글_작성자_글_작성자가_아니며_포스트의_비밀번호도_일치하지_않으면_수정할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(other)
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.update(other, "update", true, "12");
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
            void 댓글_작성자가_포스트_작성자인_경우에만_수정할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(postWriter)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    comment.update(postWriter, "update", true, null);
                });
                assertThat(comment.getContent()).isEqualTo("update");
            }

            @Test
            void 포스트_작성자가_아니면_수정할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(true)
                        .writer(other)
                        .build();

                // when & then
                assertThatThrownBy(() -> {
                    comment.update(other, "update", true, null);
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
        void 자신의_댓글인_경우_제거할_수_있다() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertDoesNotThrow(() ->
                    comment.delete(member, commentDeleteService, null)
            );
        }

        @Test
        void 자신의_댓글이_아닌_경우_예외() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    comment.delete(other, commentDeleteService, null)
            ).isInstanceOf(NoAuthorityForCommentException.class);
        }

        @Test
        void 포스트_작성자는_모든_댓글_삭제_가능하다() {
            // given
            AuthComment comment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(false)
                    .writer(member)
                    .build();

            AuthComment secretComment = AuthComment.builder()
                    .content("내용")
                    .post(post)
                    .secret(true)
                    .writer(member)
                    .build();

            // when & then
            assertDoesNotThrow(() -> {
                comment.delete(postWriter, commentDeleteService, null);
                secretComment.delete(postWriter, commentDeleteService, null);
            });
        }

        @Nested
        class 공개_포스트의_댓글을_삭제하는_경우 {

            @Test
            void 댓글_작성자라면_가능하다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(other)
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(other, commentDeleteService, null)
                );
            }
        }

        @Nested
        class 보호_포스트의_댓글을_삭제하는_경우 {

            private final Post post = Post.builder()
                    .writer(postWriter)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 댓글_작성자이며_포스트의_비밀번호가_일치하면_삭제할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(other)
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(other, commentDeleteService, "1234")
                );
            }

            @Test
            void 댓글_작성자가_포스트_작성자라면_삭제할_수_있다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(postWriter)
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(postWriter, commentDeleteService, null)
                );
            }

            @Test
            void 댓글_작성자가_포스트_작성자가_아니며_포스트의_비밀번호도_일치하지_않으면_삭제할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(other)
                        .build();

                // when & then
                assertThatThrownBy(() ->
                        comment.delete(other, commentDeleteService, "wrong")
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
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(postWriter)
                        .build();
                AuthComment otherComment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(other)
                        .build();

                // when & then
                assertDoesNotThrow(() ->
                        comment.delete(postWriter, commentDeleteService, null)
                );
                assertDoesNotThrow(() ->
                        otherComment.delete(postWriter, commentDeleteService, null)
                );
            }

            @Test
            void 포스트_작성자가_아니면_삭제할_수_없다() {
                // given
                AuthComment comment = AuthComment.builder()
                        .content("내용")
                        .post(post)
                        .secret(false)
                        .writer(other)
                        .build();

                // when & then
                assertThatThrownBy(() ->
                        comment.delete(other, commentDeleteService, null)
                ).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }
    }
}
