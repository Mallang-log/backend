package com.mallang.post.domain;

import static com.mallang.auth.MemberFixture.말랑;
import static com.mallang.auth.MemberFixture.회원;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.visibility.PostVisibilityPolicy;
import com.mallang.post.exception.AlreadyLikedPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 좋아요(PostLike) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeTest {

    private final PostLikeValidator postLikeValidator = mock(PostLikeValidator.class);
    private final Member mallang = 말랑(1L);
    private final Member other = 회원(2L, "ohter");
    private final Blog blog = new Blog("mallang", mallang);
    private final Post post = Post.builder()
            .title("제목")
            .content("내용")
            .writer(mallang)
            .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
            .blog(blog)
            .build();

    @Nested
    class 좋아요_시 {

        @Test
        void 클릭_시_포스트의_좋아요_수가_1_증가한다() {
            // given
            PostLike postLike = new PostLike(post, mallang);

            // when
            postLike.click(postLikeValidator, null);

            // then
            assertThat(post.getLikeCount()).isEqualTo(1);
        }

        @Test
        void 이미_좋아요_누른_포스트에_대해서는_좋아요를_누를_수_없다() {
            // given
            PostLike postLike = new PostLike(post, mallang);
            willThrow(AlreadyLikedPostException.class)
                    .given(postLikeValidator)
                    .validateClickLike(post, mallang);

            // when
            assertThatThrownBy(() -> {
                postLike.click(postLikeValidator, null);
            }).isInstanceOf(AlreadyLikedPostException.class);

            // then
            assertThat(post.getLikeCount()).isZero();
        }

        @Nested
        class 공개_포스트인_경우 {

            @Test
            void 누구나_좋아요가_가능하다() {
                // given
                PostLike postLike = new PostLike(post, other);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.click(postLikeValidator, null);
                });
                assertThat(post.getLikeCount()).isEqualTo(1);
            }
        }

        @Nested
        class 보호_포스트인_경우 {

            private final Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 비밀번호가_일치하면_좋아요할_수_있다() {
                // given
                PostLike postLike = new PostLike(post, other);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.click(postLikeValidator, "1234");
                });
                assertThat(post.getLikeCount()).isEqualTo(1);
            }

            @Test
            void 포스트_작성자라면_좋아요할_수_있다() {
                // given
                PostLike postLike = new PostLike(post, mallang);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.click(postLikeValidator, null);
                });
                assertThat(post.getLikeCount()).isEqualTo(1);
            }

            @Test
            void 포스트글_작성자가_아니며_비밀번호도_일치하지_않으면_좋아요할_수_없다() {
                // given
                PostLike postLike = new PostLike(post, other);

                // when & then
                assertThatThrownBy(() -> {
                    postLike.click(postLikeValidator, "12345");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(post.getLikeCount()).isZero();
            }
        }

        @Nested
        class 비공개_포스트인_경우 {

            private final Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                    .blog(blog)
                    .build();

            @Test
            void 포스트_작성자만_좋아요할_수_있다() {
                // given
                PostLike postLike = new PostLike(post, mallang);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.click(postLikeValidator, null);
                });
                assertThat(post.getLikeCount()).isEqualTo(1);
            }

            @Test
            void 포스트_작성자가_아니면_좋아요할_수_없다() {
                // given
                PostLike postLike = new PostLike(post, other);

                // when & then
                assertThatThrownBy(() -> {
                    postLike.click(postLikeValidator, null);
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(post.getLikeCount()).isZero();
            }
        }
    }

    @Nested
    class 좋아요_취소_시 {

        @Test
        void 취소_시_포스트의_좋아요_수가_1_감소한다() {
            // given
            PostLike postLike = new PostLike(post, mallang);
            postLike.click(postLikeValidator, null);

            // when
            postLike.cancel(null);

            // then
            assertThat(post.getLikeCount()).isZero();
        }

        @Nested
        class 공개_포스트인_경우 {

            @Test
            void 누구나_접근_가능하다() {
                // given
                PostLike postLike = new PostLike(post, other);
                postLike.click(postLikeValidator, null);

                // when
                postLike.cancel(null);

                // then
                assertThat(post.getLikeCount()).isZero();
            }
        }

        @Nested
        class 보호_포스트인_경우 {

            private final Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .blog(blog)
                    .build();

            @Test
            void 비밀번호가_일치하면_접근할_수_있다() {
                // given
                PostLike postLike = new PostLike(post, other);
                postLike.click(postLikeValidator, "1234");

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.cancel("1234");
                });
                assertThat(post.getLikeCount()).isZero();
            }

            @Test
            void 글_작성자라면_접근할_수_있다() {
                // given
                PostLike postLike = new PostLike(post, mallang);
                postLike.click(postLikeValidator, null);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.cancel(null);
                });
                assertThat(post.getLikeCount()).isZero();
            }

            @Test
            void 글_작성자가_아니며_비밀번호도_일치하지_않으면_접근할_수_없다() {
                // given
                PostLike postLike = new PostLike(post, other);
                postLike.click(postLikeValidator, "1234");

                // when & then
                assertThatThrownBy(() -> {
                    postLike.cancel("wrong");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(post.getLikeCount()).isEqualTo(1);
            }
        }

        @Nested
        class 비공개_포스트인_경우 {

            @Test
            void 포스트_작성자만_접근할_수_있다() {
                // given
                Post post = Post.builder()
                        .title("제목")
                        .content("내용")
                        .writer(mallang)
                        .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                        .blog(blog)
                        .build();
                PostLike postLike = new PostLike(post, mallang);
                postLike.click(postLikeValidator, null);

                // when & then
                assertDoesNotThrow(() -> {
                    postLike.cancel(null);
                });
                assertThat(post.getLikeCount()).isZero();
            }

            @Test
            void 포스트_작성자가_아니면_접근할_수_없다() {
                // given
                Post post = Post.builder()
                        .title("제목")
                        .content("내용")
                        .writer(mallang)
                        .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
                        .blog(blog)
                        .build();
                PostLike postLike = new PostLike(post, other);
                postLike.click(postLikeValidator, null);
                post.update(mallang.getId(),
                        "up",
                        "up",
                        null,
                        new PostIntro("update"),
                        new PostVisibilityPolicy(PRIVATE, null),
                        null, emptyList());

                // when & then
                assertThatThrownBy(() -> {
                    postLike.cancel("wrong");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
                assertThat(post.getLikeCount()).isEqualTo(1);
            }
        }
    }
}
