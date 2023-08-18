package com.mallang.post.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static com.mallang.member.domain.OauthServerType.GITHUB;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.mallang.category.domain.Category;
import com.mallang.category.domain.CategoryValidator;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.member.MemberFixture;
import com.mallang.member.domain.Member;
import com.mallang.member.domain.OauthId;
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

    private final CategoryValidator categoryValidator = mock(CategoryValidator.class);
    private final Member writer = memberBuilder()
            .id(1L)
            .oauthId(new OauthId("1", GITHUB))
            .nickname("말랑")
            .profileImageUrl("https://mallang.com")
            .build();

    @Test
    void 카테고리를_없앨_수_있다() {
        // given
        Category category = Category.create("카테고리", writer, null, categoryValidator);
        Post post = Post.builder()
                .title("제목")
                .content("내용")
                .member(writer)
                .category(category)
                .build();

        // when
        post.removeCategory();

        // then
        assertThat(post.getCategory()).isNull();
    }

    @Nested
    class 저장_시 {

        @Test
        void 태그들도_함께_세팅되어_생성된다() {
            // given
            Post taggedPost = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(writer)
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
                    .member(writer)
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
                            .member(writer)
                            .tags(List.of("태그1", "태그1"))
                            .build()
            ).isInstanceOf(DuplicatedTagsInPostException.class);
        }

        @Test
        void 카테고리를_설정할_수_있다() {
            // given
            Category category = Category.create("카테고리", writer, null, categoryValidator);

            // when
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(writer)
                    .category(category)
                    .build();

            // then
            assertThat(post.getCategory().getName()).isEqualTo("카테고리");
        }

        @Test
        void 작성자가_생성한_카테고리가_아닌_경우_예외() {
            // given
            Member other = MemberFixture.회원(2L, "other");
            Category category = Category.create("other", other, null, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    Post.builder()
                            .title("제목")
                            .content("내용")
                            .member(writer)
                            .category(category)
                            .build()
            ).isInstanceOf(NoAuthorityUseCategoryException.class);
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
                    .member(writer)
                    .build();

            // when
            post.update(writer.getId(), "수정제목", "수정내용", null);

            // then
            assertThat(post.getTitle()).isEqualTo("수정제목");
            assertThat(post.getContent()).isEqualTo("수정내용");
        }

        @Test
        void 작성자가_아니면_예외() {
            // given
            Post post = Post.builder()
                    .title("제목")
                    .content("내용")
                    .member(writer)
                    .build();

            // when
            assertThatThrownBy(() ->
                    post.update(writer.getId() + 1, "수정제목", "수정내용", null)
            ).isInstanceOf(NoAuthorityUpdatePostException.class);

            // then
            assertThat(post.getTitle()).isEqualTo("제목");
            assertThat(post.getContent()).isEqualTo("내용");
        }
    }
}
