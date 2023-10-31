package com.mallang.post.domain;

import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.category.CategoryFixture.하위_카테고리;
import static com.mallang.member.MemberFixture.동훈;
import static com.mallang.member.MemberFixture.말랑;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.IsNotBlogOwnerException;
import com.mallang.category.domain.Category;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.member.domain.Member;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityUpdatePostException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트(Post) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostTest {

    private final Member mallang = 말랑(1L);
    private final Member otherMember = 동훈(3L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Blog otherBlog = new Blog("ohter", otherMember);
    private final Category springCategory = 루트_카테고리("Spring", mallang, blog);
    private final Category jpaCategory = 하위_카테고리("JPA", mallang, blog, springCategory);
    private final Category otherMemberCategory = 루트_카테고리("otherMemberCategory", otherMember, otherBlog);

    @Test
    void 카테고리를_없앨_수_있다() {
        // given
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .member(mallang)
                .blog(blog)
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
        void 태그들도_함께_세팅되어_생성된다() {
            // given
            Post taggedPost = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(mallang)
                    .blog(blog)
                    .tags(List.of("tag1", "tag2"))
                    .build();

            // when & then
            assertThat(taggedPost.getTags())
                    .extracting(Tag::getContent)
                    .containsExactly("tag1", "tag2");
        }

        @Test
        void 태그가_없어도_된다() {
            // given
            Post taggedPost = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(mallang)
                    .blog(blog)
                    .build();

            // when & then
            assertThat(taggedPost.getTags()).isEmpty();
        }

        @Test
        void 한_포스트에_동일한_태그가_붙을_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    Post.builder()
                            .title("제목")
                            .content("내용")
                            .member(mallang)
                            .blog(blog)
                            .tags(List.of("태그1", "태그1"))
                            .build()
            ).isInstanceOf(DuplicatedTagsInPostException.class);
        }

        @Test
        void 카테고리를_설정할_수_있다() {
            // when
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(mallang)
                    .blog(blog)
                    .category(jpaCategory)
                    .build();

            // then
            assertThat(post.getCategory().getName()).isEqualTo("JPA");
        }

        @Test
        void 작성자가_생성한_카테고리가_아닌_경우_예외() {
            // when & then
            assertThatThrownBy(() ->
                    Post.builder()
                            .title("제목")
                            .content("내용")
                            .member(mallang)
                            .blog(blog)
                            .category(otherMemberCategory)
                            .build()
            ).isInstanceOf(NoAuthorityUseCategoryException.class);
        }

        @Test
        void 자신의_블로그가_아니라면_예외() {
            // given

            // when & then
            assertThatThrownBy(() ->
                    Post.builder()
                            .title("제목")
                            .content("내용")
                            .member(mallang)
                            .blog(otherBlog)
                            .build()
            ).isInstanceOf(IsNotBlogOwnerException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 수정에_성공한다() {
            // given
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(mallang)
                    .blog(blog)
                    .tags(List.of("태그1"))
                    .build();

            // when
            post.update(mallang.getId(), "수정제목", "수정내용", null, List.of("태그2"));

            // then
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
            assertThat(post.getTags())
                    .extracting(Tag::getContent)
                    .containsExactly("태그2");
        }

        @Test
        void 작성자가_아니면_예외() {
            // given
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(mallang)
                    .blog(blog)
                    .build();

            // when
            assertThatThrownBy(() ->
                    post.update(otherMember.getId(), "수정제목", "수정내용", null, emptyList())
            ).isInstanceOf(NoAuthorityUpdatePostException.class);

            // then
            assertThat(post.getTitle()).isEqualTo("제목");
            assertThat(post.getContent()).isEqualTo("내용");
        }
    }
}
