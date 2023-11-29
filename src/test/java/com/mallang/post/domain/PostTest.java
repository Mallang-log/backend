package com.mallang.post.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.category.CategoryFixture.하위_카테고리;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PRIVATE;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PROTECTED;
import static com.mallang.post.domain.PostVisibilityPolicy.Visibility.PUBLIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.domain.Category;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityAccessPostException;
import com.mallang.post.exception.NoAuthorityPostException;
import com.mallang.post.exception.PostLikeCountNegativeException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("포스트 (Post) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostTest {

    private final Member mallang = 깃허브_말랑(1L);
    private final Member otherMember = 깃허브_동훈(3L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Blog otherBlog = new Blog("ohter", otherMember);
    private final Category springCategory = 루트_카테고리("Spring", mallang, blog);
    private final Category jpaCategory = 하위_카테고리("JPA", mallang, blog, springCategory);
    private final Category otherCategory = 루트_카테고리("Spring", otherMember, otherBlog);

    @Test
    void Id가_같으면_동일하다() {
        // given
        Post post1 = Post.builder()
                .blog(blog)
                .writer(mallang)
                .title("1234")
                .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
                .build();
        Post post2 = Post.builder()
                .blog(blog)
                .writer(mallang)
                .title("5678")
                .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
                .build();
        ReflectionTestUtils.setField(post1, "postId", new PostId(1L, 2L));
        ReflectionTestUtils.setField(post2, "postId", new PostId(1L, 2L));
        Post same = post1;

        // when & then
        assertThat(post1)
                .isEqualTo(same)
                .hasSameHashCodeAs(post2)
                .isEqualTo(post2)
                .isNotEqualTo(new Object());
    }

    @Test
    void 카테고리를_없앨_수_있다() {
        // given
        Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .category(springCategory)
                .build();

        // when
        post.removeCategory();

        // then
        assertThat(post.getCategory()).isNull();
    }

    @Nested
    class 생성_시 {

        @Test
        void 포스트_작성자와_블로그_주인이_다른_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                Post.builder()
                        .blog(otherBlog)
                        .writer(mallang)
                        .build();
            }).isInstanceOf(NoAuthorityBlogException.class);
            assertThatThrownBy(() -> {
                Post.builder()
                        .blog(blog)
                        .writer(otherMember)
                        .build();
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 다른_사람의_카테고리를_섫정한_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                Post.builder()
                        .blog(blog)
                        .writer(mallang)
                        .category(otherCategory)
                        .build();
            }).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 태그들도_함께_세팅되어_생성된다() {
            // given
            Post taggedPost = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .tags(List.of("tag1", "tag2"))
                    .build();

            // when & then
            assertThat(taggedPost.getTags())
                    .containsExactly("tag1", "tag2");
        }

        @Test
        void 태그가_없어도_된다() {
            // given
            Post taggedPost = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(taggedPost.getTags()).isEmpty();
        }

        @Test
        void 한_포스트에_동일한_태그가_붙을_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    Post.builder()
                            .blog(blog)
                            .title("제목")
                            .bodyText("내용")
                            .writer(mallang)
                            .tags(List.of("태그1", "태그1"))
                            .build()
            ).isInstanceOf(DuplicatedTagsInPostException.class);
        }

        @Test
        void 카테고리를_설정할_수_있다() {
            // when
            Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .category(jpaCategory)
                    .build();

            // then
            assertThat(post.getCategory().getName()).isEqualTo("JPA");
        }

        @Test
        void 썸네일_사진_설정이_가능하다() {
            // given
            Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .postThumbnailImageName("thumbnail")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(post.getPostThumbnailImageName()).isEqualTo("thumbnail");
        }

        @Test
        void 썸네일은_없어도_된다() {
            // given
            Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(post.getPostThumbnailImageName()).isNull();
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 수정에_성공한다() {
            // given
            Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "123"))
                    .tags(List.of("태그1"))
                    .build();

            // when
            post.update(
                    new PostVisibilityPolicy(PRIVATE),
                    "수정제목",
                    "수정내용",
                    "postThumbnailImageName",
                    new PostIntro("수정인트로"),
                    null,
                    List.of("태그2")
            );

            // then
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getBodyText()).isEqualTo("수정내용");
            assertThat(post.getVisibilityPolish().getVisibility()).isEqualTo(PRIVATE);
            assertThat(post.getTags())
                    .containsExactly("태그2");
        }

        @Test
        void 다른_사람의_카테고리로_수정_시_예외() {
            // given
            Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "123"))
                    .tags(List.of("태그1"))
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                post.update(
                        new PostVisibilityPolicy(PRIVATE),
                        "수정제목",
                        "수정내용",
                        "postThumbnailImageName",
                        new PostIntro("수정인트로"),
                        otherCategory,
                        Collections.emptyList()
                );
            }).isInstanceOf(NoAuthorityCategoryException.class);
        }
    }

    @Nested
    class 삭제_시 {

        private final Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .build();

        @Test
        void 해당_글_작성자만_삭제할_수_있다() {
            // when & then
            assertDoesNotThrow(() -> {
                post.delete();
            });
        }

        @Test
        void 포스트_삭제_이벤트가_발행된다() {
            // when
            post.delete();

            // then
            List<Object> domainEvents = (List<Object>) ReflectionTestUtils.getField(post, "domainEvents");
            assertThat(domainEvents.get(0)).isInstanceOf(PostDeleteEvent.class);
        }
    }

    @Nested
    class 접근_권한_확인_시 {

        @Nested
        class 공개_포스트인_경우 {

            @Test
            void 누구나_접근_가능하다() {
                // given
                Post post = Post.builder()
                        .blog(blog)
                        .title("제목")
                        .bodyText("내용")
                        .writer(mallang)
                        .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
                        .category(springCategory)
                        .build();

                // when & then
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(mallang, null);
                });
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(otherMember, null);
                });
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(null, null);
                });
            }
        }

        @Nested
        class 보호_포스트인_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
                    .category(springCategory)
                    .build();

            @Test
            void 비밀번호가_일치하면_접근할_수_있다() {
                // when & then
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(null, "1234");
                });
            }

            @Test
            void 글_작성자라면_접근할_수_있다() {
                // when & then
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(mallang, null);
                });
            }

            @Test
            void 글_작성자가_아니며_비밀번호도_일치하지_않으면_접근할_수_없다() {
                // when & then
                assertThatThrownBy(() -> {
                    post.validatePostAccessibility(otherMember, "12345");
                }).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }

        @Nested
        class 비공개_포스트인_경우 {

            private final Post post = Post.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                    .category(springCategory)
                    .build();

            @Test
            void 포스트_작성자만_접근할_수_있다() {
                // when & then
                assertDoesNotThrow(() -> {
                    post.validatePostAccessibility(mallang, null);
                });
            }

            @Test
            void 포스트_작성자가_아니면_접근할_수_없다() {
                // when & then
                assertThatThrownBy(() -> {
                    post.validatePostAccessibility(otherMember, null);
                }).isInstanceOf(NoAuthorityAccessPostException.class);
            }
        }
    }

    @Test
    void 작성자_검증() {
        // given
        Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
                .category(springCategory)
                .build();

        // when & then
        assertDoesNotThrow(() -> {
            post.validateWriter(mallang);
        });
        assertThatThrownBy(() -> {
            post.validateWriter(otherMember);
        }).isInstanceOf(NoAuthorityPostException.class);
    }

    @Test
    void 좋아요를_누를_수_있다() {
        // given
        Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .build();

        // when
        post.clickLike();

        // then
        assertThat(post.getLikeCount()).isEqualTo(1);
    }

    @Test
    void 좋아료를_취소한다() {
        Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .build();
        post.clickLike();
        post.clickLike();
        post.clickLike();

        // when
        post.cancelLike();

        // then
        assertThat(post.getLikeCount()).isEqualTo(2);
    }

    @Test
    void 좋아요는_음수가_될_수_없다() {
        // given
        Post post = Post.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .build();

        // when
        assertThatThrownBy(() -> {
            post.cancelLike();
        }).isInstanceOf(PostLikeCountNegativeException.class);

        // then
        assertThat(post.getLikeCount()).isZero();
    }
}
