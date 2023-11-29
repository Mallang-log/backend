package com.mallang.post.domain.draft;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.category.CategoryFixture.하위_카테고리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.domain.Category;
import com.mallang.category.exception.NoAuthorityCategoryException;
import com.mallang.post.exception.DuplicatedTagsInPostException;
import com.mallang.post.exception.NoAuthorityPostException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("임시 글 (Draft) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DraftTest {

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
        Draft draft1 = Draft.builder()
                .blog(blog)
                .writer(mallang)
                .title("1234")
                .build();
        Draft draft2 = Draft.builder()
                .blog(blog)
                .writer(mallang)
                .title("5678")
                .build();
        ReflectionTestUtils.setField(draft1, "id", 1L);
        ReflectionTestUtils.setField(draft2, "id", 1L);
        Draft same = draft1;

        // when & then
        assertThat(draft1)
                .isEqualTo(same)
                .hasSameHashCodeAs(draft2)
                .isEqualTo(draft2)
                .isNotEqualTo(new Object());
    }

    @Test
    void 카테고리를_없앨_수_있다() {
        // given
        Draft draft = Draft.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .category(springCategory)
                .build();

        // when
        draft.removeCategory();

        // then
        assertThat(draft.getCategory()).isNull();
    }

    @Nested
    class 생성_시 {

        @Test
        void 임시_글_작성자와_블로그_주인이_다른_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                Draft.builder()
                        .blog(otherBlog)
                        .writer(mallang)
                        .build();
            }).isInstanceOf(NoAuthorityBlogException.class);
            assertThatThrownBy(() -> {
                Draft.builder()
                        .blog(blog)
                        .writer(otherMember)
                        .build();
            }).isInstanceOf(NoAuthorityBlogException.class);
        }

        @Test
        void 다른_사람의_카테고리를_섫정한_경우_예외() {
            // when & then
            assertThatThrownBy(() -> {
                Draft.builder()
                        .blog(blog)
                        .writer(mallang)
                        .category(otherCategory)
                        .build();
            }).isInstanceOf(NoAuthorityCategoryException.class);
        }

        @Test
        void 태그들도_함께_세팅되어_생성된다() {
            // given
            Draft taggedDraft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .postIntro("intro")
                    .postThumbnailImageName("thumbnail")
                    .writer(mallang)
                    .tags(List.of("tag1", "tag2"))
                    .build();

            // when & then
            assertThat(taggedDraft.getBlog()).isEqualTo(blog);
            assertThat(taggedDraft.getTitle()).isEqualTo("제목");
            assertThat(taggedDraft.getBodyText()).isEqualTo("내용");
            assertThat(taggedDraft.getPostIntro()).isEqualTo("intro");
            assertThat(taggedDraft.getWriter()).isEqualTo(mallang);
            assertThat(taggedDraft.getTags())
                    .containsExactly("tag1", "tag2");
        }

        @Test
        void 태그가_없어도_된다() {
            // given
            Draft taggedDraft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(taggedDraft.getTags()).isEmpty();
        }

        @Test
        void 한_임시_글에_동일한_태그가_붙을_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    Draft.builder()
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
            Draft draft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .category(jpaCategory)
                    .build();

            // then
            assertThat(draft.getCategory().getName()).isEqualTo("JPA");
        }

        @Test
        void 썸네일_사진_설정이_가능하다() {
            // given
            Draft draft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .postThumbnailImageName("thumbnail")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(draft.getPostThumbnailImageName()).isEqualTo("thumbnail");
        }

        @Test
        void 썸네일은_없어도_된다() {
            // given
            Draft draft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .build();

            // when & then
            assertThat(draft.getPostThumbnailImageName()).isNull();
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 수정에_성공한다() {
            // given
            Draft draft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .tags(List.of("태그1"))
                    .build();

            // when
            draft.update(
                    "수정제목",
                    "수정내용",
                    "postThumbnailImageName",
                    "수정인트로",
                    null,
                    List.of("태그2")
            );

            // then
            assertThat(draft.getTitle()).isEqualTo("수정제목");
            assertThat(draft.getBodyText()).isEqualTo("수정내용");
            assertThat(draft.getTags())
                    .containsExactly("태그2");
        }

        @Test
        void 다른_사람의_카테고리로_수정_시_예외() {
            // given
            Draft draft = Draft.builder()
                    .blog(blog)
                    .title("제목")
                    .bodyText("내용")
                    .writer(mallang)
                    .tags(List.of("태그1"))
                    .build();

            // when & then
            assertThatThrownBy(() -> {
                draft.update(
                        "수정제목",
                        "수정내용",
                        "postThumbnailImageName",
                        "수정인트로",
                        otherCategory,
                        Collections.emptyList()
                );
            }).isInstanceOf(NoAuthorityCategoryException.class);
        }
    }

    @Test
    void 작성자_검증() {
        // given
        Draft draft = Draft.builder()
                .blog(blog)
                .title("제목")
                .bodyText("내용")
                .writer(mallang)
                .category(springCategory)
                .build();

        // when & then
        assertDoesNotThrow(() -> {
            draft.validateWriter(mallang);
        });
        assertThatThrownBy(() -> {
            draft.validateWriter(otherMember);
        }).isInstanceOf(NoAuthorityPostException.class);
    }
}
