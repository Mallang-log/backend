package com.mallang.post.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.post.PostCategoryFixture.루트_카테고리;
import static com.mallang.post.PostCategoryFixture.하위_카테고리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityPostCategoryException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("포스트 내용 (PostContent) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostContentTest {

    private final Member mallang = 깃허브_말랑(1L);
    private final Member otherMember = 깃허브_동훈(3L);
    private final Blog blog = new Blog("mallang", mallang);
    private final Blog otherBlog = new Blog("ohter", otherMember);
    private final PostCategory springPostCategory = 루트_카테고리("Spring", mallang, blog);
    private final PostCategory jpaPostCategory = 하위_카테고리("JPA", mallang, blog, springPostCategory);
    private final PostCategory otherPostCategory = 루트_카테고리("Spring", otherMember, otherBlog);

    @Test
    void 카테고리를_없앨_수_있다() {
        // given
        PostContent postContent = PostContent.builder()
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .category(springPostCategory)
                .build();

        // when
        PostContent removeCategory = postContent.removeCategory();

        // then
        assertThat(removeCategory.getCategory()).isNull();
        assertThat(removeCategory)
                .usingRecursiveComparison()
                .ignoringFields("category")
                .isEqualTo(postContent);
    }

    @Nested
    class 생성_시 {

        @Test
        void 다른_사람의_카테고리를_섫정한_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                PostContent.builder()
                        .writer(mallang)
                        .category(otherPostCategory)
                        .build();
            }).isInstanceOf(NoAuthorityPostCategoryException.class);
        }

        @Test
        void 태그들도_함께_세팅되어_생성된다() {
            // given
            PostContent taggedPost = PostContent.builder()
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
            PostContent taggedPost = PostContent.builder()
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
                    PostContent.builder()
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
            PostContent postContent = PostContent.builder()
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .category(jpaPostCategory)
                    .build();

            // then
            assertThat(postContent.getCategory().getName()).isEqualTo("JPA");
        }
    }

    @Test
    void 작성자_확인() {
        // given
        PostContent postContent = PostContent.builder()
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .tags(List.of("태그1"))
                .build();

        // when & then
        assertThat(postContent.isWriter(mallang)).isTrue();
        assertThat(postContent.isWriter(otherMember)).isFalse();
    }
}
