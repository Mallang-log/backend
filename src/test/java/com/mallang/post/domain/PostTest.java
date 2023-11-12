//package com.mallang.post.domain;
//
//import static com.mallang.auth.MemberFixture.동훈;
//import static com.mallang.auth.MemberFixture.말랑;
//import static com.mallang.category.CategoryFixture.루트_카테고리;
//import static com.mallang.category.CategoryFixture.하위_카테고리;
//import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PRIVATE;
//import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PROTECTED;
//import static com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility.PUBLIC;
//import static java.util.Collections.emptyList;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//
//import com.mallang.auth.domain.Member;
//import com.mallang.blog.domain.Blog;
//import com.mallang.blog.exception.IsNotBlogOwnerException;
//import com.mallang.category.domain.Category;
//import com.mallang.category.exception.NoAuthorityUseCategoryException;
//import com.mallang.post.domain.visibility.PostVisibilityPolicy;
//import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
//import com.mallang.post.exception.DuplicatedTagsInPostException;
//import com.mallang.post.exception.NoAuthorityAccessPostException;
//import com.mallang.post.exception.NoAuthorityDeletePostException;
//import com.mallang.post.exception.NoAuthorityUpdatePostException;
//import com.mallang.post.exception.PostLikeCountNegativeException;
//import java.util.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.DisplayNameGeneration;
//import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//
//@DisplayName("포스트(Post) 은(는)")
//@SuppressWarnings("NonAsciiCharacters")
//@DisplayNameGeneration(ReplaceUnderscores.class)
//class PostTest {
//
//    private final Member mallang = 말랑(1L);
//    private final Member otherMember = 동훈(3L);
//    private final Blog blog = new Blog("mallang", mallang);
//    private final Blog otherBlog = new Blog("ohter", otherMember);
//    private final Category springCategory = 루트_카테고리("Spring", mallang, blog);
//    private final Category jpaCategory = 하위_카테고리("JPA", mallang, blog, springCategory);
//    private final Category otherMemberCategory = 루트_카테고리("otherMemberCategory", otherMember, otherBlog);
//
//    @Test
//    void 카테고리를_없앨_수_있다() {
//        // given
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .writer(mallang)
//                .blog(blog)
//                .category(springCategory)
//                .build();
//
//        // when
//        post.removeCategory();
//
//        // then
//        assertThat(post.getCategory()).isNull();
//    }
//
//    @Nested
//    class 생성_시 {
//
//        @Test
//        void 태그들도_함께_세팅되어_생성된다() {
//            // given
//            Post taggedPost = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .blog(blog)
//                    .tags(List.of("tag1", "tag2"))
//                    .build();
//
//            // when & then
//            assertThat(taggedPost.getTags())
//                    .extracting(Tag::getContent)
//                    .containsExactly("tag1", "tag2");
//        }
//
//        @Test
//        void 태그가_없어도_된다() {
//            // given
//            Post taggedPost = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .blog(blog)
//                    .build();
//
//            // when & then
//            assertThat(taggedPost.getTags()).isEmpty();
//        }
//
//        @Test
//        void 한_포스트에_동일한_태그가_붙을_수_없다() {
//            // when & then
//            assertThatThrownBy(() ->
//                    Post.builder()
//                            .title("제목")
//                            .content("내용")
//                            .writer(mallang)
//                            .blog(blog)
//                            .tags(List.of("태그1", "태그1"))
//                            .build()
//            ).isInstanceOf(DuplicatedTagsInPostException.class);
//        }
//
//        @Test
//        void 카테고리를_설정할_수_있다() {
//            // when
//            Post post = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .blog(blog)
//                    .category(jpaCategory)
//                    .build();
//
//            // then
//            assertThat(post.getCategory().getName()).isEqualTo("JPA");
//        }
//
//        @Test
//        void 작성자가_생성한_카테고리가_아닌_경우_예외() {
//            // when & then
//            assertThatThrownBy(() ->
//                    Post.builder()
//                            .title("제목")
//                            .content("내용")
//                            .writer(mallang)
//                            .blog(blog)
//                            .category(otherMemberCategory)
//                            .build()
//            ).isInstanceOf(NoAuthorityUseCategoryException.class);
//        }
//
//        @Test
//        void 자신의_블로그가_아니라면_예외() {
//            // when & then
//            assertThatThrownBy(() ->
//                    Post.builder()
//                            .title("제목")
//                            .content("내용")
//                            .writer(mallang)
//                            .blog(otherBlog)
//                            .build()
//            ).isInstanceOf(IsNotBlogOwnerException.class);
//        }
//    }
//
//    @Nested
//    class 수정_시 {
//
//        @Test
//        void 수정에_성공한다() {
//            // given
//            Post post = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .blog(blog)
//                    .visibilityPolish(new PostVisibilityPolicy(Visibility.PROTECTED, "123"))
//                    .tags(List.of("태그1"))
//                    .build();
//
//            // when
//            post.update(mallang.getId(), "수정제목", "수정내용",
//                    new PostVisibilityPolicy(PRIVATE), null,
//                    List.of("태그2")
//            );
//
//            // then
//            assertThat(post.getTitle()).isEqualTo("수정제목");
//            assertThat(post.getContent()).isEqualTo("수정내용");
//            assertThat(post.getVisibilityPolish().getVisibility()).isEqualTo(PRIVATE);
//            assertThat(post.getTags())
//                    .extracting(Tag::getContent)
//                    .containsExactly("태그2");
//        }
//
//        @Test
//        void 작성자가_아니면_예외() {
//            // given
//            Post post = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .blog(blog)
//                    .build();
//
//            // when
//            assertThatThrownBy(() ->
//                    post.update(otherMember.getId(), "수정제목", "수정내용", new PostVisibilityPolicy(PUBLIC), null,
//                            emptyList())
//            ).isInstanceOf(NoAuthorityUpdatePostException.class);
//
//            // then
//            assertThat(post.getTitle()).isEqualTo("제목");
//            assertThat(post.getContent()).isEqualTo("내용");
//        }
//    }
//
//    @Nested
//    class 삭제_시 {
//
//        private final Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .writer(mallang)
//                .blog(blog)
//                .build();
//
//        @Test
//        void 해당_글_작성자만_삭제할_수_있다() {
//            // when & then
//            assertDoesNotThrow(() -> {
//                post.delete(mallang.getId());
//            });
//        }
//
//        @Test
//        void 포스트_삭제_이벤트가_발행된다() {
//            // when
//            post.delete(mallang.getId());
//
//            // then
//            assertThat(post.domainEvents().get(0)).isInstanceOf(PostDeleteEvent.class);
//        }
//
//        @Test
//        void 해당_글_작성자가_아니면_예외() {
//            // when
//            assertThatThrownBy(() -> {
//                post.delete(mallang.getId() + 1);
//            }).isInstanceOf(NoAuthorityDeletePostException.class);
//
//            // then
//            assertThat(post.domainEvents()).isEmpty();
//        }
//    }
//
//    @Nested
//    class 접근_권한_확인_시 {
//
//        @Nested
//        class 공개_포스트인_경우 {
//
//            @Test
//            void 누구나_접근_가능하다() {
//                // given
//                Post post = Post.builder()
//                        .title("제목")
//                        .content("내용")
//                        .writer(mallang)
//                        .visibilityPolish(new PostVisibilityPolicy(PUBLIC, null))
//                        .blog(blog)
//                        .category(springCategory)
//                        .build();
//
//                // when & then
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(mallang.getId(), null);
//                });
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(100L, null);
//                });
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(null, null);
//                });
//            }
//        }
//
//        @Nested
//        class 보호_포스트인_경우 {
//
//            private final Post post = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .visibilityPolish(new PostVisibilityPolicy(PROTECTED, "1234"))
//                    .blog(blog)
//                    .category(springCategory)
//                    .build();
//
//            @Test
//            void 비밀번호가_일치하면_접근할_수_있다() {
//                // when & then
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(null, "1234");
//                });
//            }
//
//            @Test
//            void 글_작성자라면_접근할_수_있다() {
//                // when & then
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(mallang.getId(), null);
//                });
//            }
//
//            @Test
//            void 글_작성자가_아니며_비밀번호도_일치하지_않으면_접근할_수_없다() {
//                // when & then
//                assertThatThrownBy(() -> {
//                    post.validatePostAccessibility(2L, "12345");
//                }).isInstanceOf(NoAuthorityAccessPostException.class);
//            }
//        }
//
//        @Nested
//        class 비공개_포스트인_경우 {
//
//            private final Post post = Post.builder()
//                    .title("제목")
//                    .content("내용")
//                    .writer(mallang)
//                    .visibilityPolish(new PostVisibilityPolicy(PRIVATE, null))
//                    .blog(blog)
//                    .category(springCategory)
//                    .build();
//
//            @Test
//            void 포스트_작성자만_접근할_수_있다() {
//                // when & then
//                assertDoesNotThrow(() -> {
//                    post.validatePostAccessibility(mallang.getId(), null);
//                });
//            }
//
//            @Test
//            void 포스트_작성자가_아니면_접근할_수_없다() {
//                // when & then
//                assertThatThrownBy(() -> {
//                    post.validatePostAccessibility(2L, null);
//                }).isInstanceOf(NoAuthorityAccessPostException.class);
//            }
//        }
//    }
//
//    @Test
//    void 좋아요를_누를_수_있다() {
//        // given
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .writer(mallang)
//                .blog(blog)
//                .build();
//
//        // when
//        post.clickLike();
//
//        // then
//        assertThat(post.getLikeCount()).isEqualTo(1);
//    }
//
//    @Test
//    void 좋아료를_취소한다() {
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .writer(mallang)
//                .blog(blog)
//                .build();
//        post.clickLike();
//        post.clickLike();
//        post.clickLike();
//
//        // when
//        post.cancelLike();
//
//        // then
//        assertThat(post.getLikeCount()).isEqualTo(2);
//    }
//
//    @Test
//    void 좋아요는_음수가_될_수_없다() {
//        // given
//        Post post = Post.builder()
//                .title("제목")
//                .content("내용")
//                .writer(mallang)
//                .blog(blog)
//                .build();
//
//        // when
//        assertThatThrownBy(() -> {
//            post.cancelLike();
//        }).isInstanceOf(PostLikeCountNegativeException.class);
//
//        // then
//        assertThat(post.getLikeCount()).isEqualTo(0);
//    }
//}
