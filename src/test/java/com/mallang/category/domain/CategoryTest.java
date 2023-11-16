package com.mallang.category.domain;

import static com.mallang.auth.MemberFixture.동훈;
import static com.mallang.auth.MemberFixture.말랑;
import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.category.CategoryFixture.하위_카테고리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityDeleteCategoryException;
import com.mallang.category.exception.NoAuthorityUpdateCategoryException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리(Category) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryTest {

    private final CategoryValidator categoryValidator = mock(CategoryValidator.class);
    private final Member mallang = 말랑(1L);
    private final Member otherMember = 동훈(2L);
    private final Blog mallangBlog = new Blog("mallang-log", mallang);
    private final Blog otherBlog = new Blog("other-log", otherMember);

    @Nested
    class 생성_시 {

        @Test
        void 하위_카테고리로_만든다() {
            // given
            Category 최상위 = Category.create("최상위", mallang, mallangBlog, null, categoryValidator);

            // when
            Category 하위 = Category.create("하위", mallang, mallangBlog, 최상위, categoryValidator);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
        }

        @Test
        void 하위_카테고리를_생성한_회원은_상위_카테고리를_생성한_회원과_같아야한다() {
            // given
            Category 최상위 = Category.create("최상위", mallang, mallangBlog, null, categoryValidator);

            // when
            assertThatThrownBy(() ->
                    Category.create("하위", otherMember, otherBlog, 최상위, categoryValidator)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            assertThat(최상위.getChildren()).isEmpty();
        }

        @Test
        void 무한_Depth_가_가능하다() {
            // given
            Category 최상위 = Category.create("최상위", mallang, mallangBlog, null, categoryValidator);
            Category 하위 = Category.create("하위", mallang, mallangBlog, 최상위, categoryValidator);

            // when
            Category 더하위 = Category.create("더하위", mallang, mallangBlog, 하위, categoryValidator);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
            assertThat(더하위.getParent()).isEqualTo(하위);
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category 최상위 = Category.create("최상위", mallang, mallangBlog, null, categoryValidator);
            Category.create("하위", mallang, mallangBlog, 최상위, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    Category.create("하위", mallang, mallangBlog, 최상위, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category.create("최상위", mallang, mallangBlog, null, categoryValidator);
            willThrow(new DuplicateCategoryNameException())
                    .given(categoryValidator)
                    .validateDuplicateRootName(mallang.getId(), "최상위");

            // when & then
            assertThatThrownBy(() ->
                    Category.create("최상위", mallang, mallangBlog, null, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 수정_시 {

        private final Category rootCategory = 루트_카테고리("루트", mallang, mallangBlog);
        private final Category childCategory = 하위_카테고리("하위", mallang, mallangBlog, rootCategory);

        @Test
        void 이름을_변경할_수_있다() {
            // when
            rootCategory.update(mallang.getId(), "말랑", null, categoryValidator);

            // then
            assertThat(rootCategory.getName()).isEqualTo("말랑");
        }

        @Test
        void 자식을_루트로_만들_수_있다() {
            // given
            Category childChildCategory = 하위_카테고리("하위의 하위", mallang, mallangBlog, childCategory);

            // when
            childCategory.update(mallang.getId(), "자식 to Root", null, categoryValidator);

            // then
            assertThat(childCategory.getParent()).isNull();
            assertThat(childChildCategory.getParent()).isEqualTo(childCategory);
        }

        @Test
        void 다른_카테고리의_하위_카테고리로_수정되는_경우_기존_자식들_역시_따라간다() {
            // given
            Category otherRootCategory = 루트_카테고리("다른 루트", mallang, mallangBlog);
            Category childChildCategory = 하위_카테고리("하위의 하위", mallang, mallangBlog, childCategory);

            // when
            childCategory.update(mallang.getId(), "자식 to otherRoot", otherRootCategory, categoryValidator);

            // then
            assertThat(childCategory.getParent()).isEqualTo(otherRootCategory);
            assertThat(childChildCategory.getParent()).isEqualTo(childCategory);
        }

        @Test
        void 자기_자신이_부모여서는_안된다() {
            // when & then
            assertThatThrownBy(() ->
                    rootCategory.update(mallang.getId(), "name", rootCategory, categoryValidator)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신보다_낮은_카테고리를_부모로_둘_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    rootCategory.update(mallang.getId(), "name", childCategory, categoryValidator)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신의_카테고리가_아니라면_수정할_수_없다() {
            // given
            Category category = 루트_카테고리("root", mallang, mallangBlog);

            // when
            assertThatThrownBy(() ->
                    category.update(otherMember.getId(), "말랑", null, categoryValidator)
            ).isInstanceOf(NoAuthorityUpdateCategoryException.class);

            // then
            assertThat(category.getName()).isEqualTo("root");
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category child2 = 하위_카테고리("child 2", mallang, mallangBlog, rootCategory);

            // when & then
            assertThatThrownBy(() ->
                    child2.update(mallang.getId(), childCategory.getName(), rootCategory, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_겹쳐서는_안된다() {
            // given
            willThrow(new DuplicateCategoryNameException())
                    .given(categoryValidator)
                    .validateDuplicateRootName(mallang.getId(), "최상위");

            // when & then
            assertThatThrownBy(() ->
                    childCategory.update(mallang.getId(), "최상위", null, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        private final Category rootCategory = 루트_카테고리("루트", mallang, mallangBlog);
        private final Category childCategory = 하위_카테고리("하위", mallang, mallangBlog, rootCategory);

        @Test
        void 자신의_카테고리가_아니면_제거할_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    childCategory.delete(otherMember.getId())
            ).isInstanceOf(NoAuthorityDeleteCategoryException.class);
        }

        @Test
        void 하위_카테고리가_존재하면_제거할_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    rootCategory.delete(mallang.getId())
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 부모_카테고리의_하위_카테고리에서도_제거된다() {
            // when
            childCategory.delete(mallang.getId());

            // then
            assertThat(rootCategory.getChildren()).isEmpty();
        }

        @Test
        void 제거_이벤트가_발핼된다() {
            // when
            childCategory.delete(mallang.getId());

            // then
            assertThat(childCategory.domainEvents().get(0))
                    .isInstanceOf(CategoryDeletedEvent.class);
        }
    }

    @Test
    void 모든_자손을_반환한다() {
        // given
        Category 최상위 = 루트_카테고리("최상위", mallang, mallangBlog);
        Category 하위 = 하위_카테고리("하위", mallang, mallangBlog, 최상위);
        Category 더하위1 = 하위_카테고리("더하위1", mallang, mallangBlog, 하위);
        Category 더하위2 = 하위_카테고리("더하위2", mallang, mallangBlog, 하위);
        Category 더더하위1 = 하위_카테고리("더더하위1", mallang, mallangBlog, 더하위1);

        // when
        List<Category> 최상위_descendants = 최상위.getDescendants();
        List<Category> 하위_descendants = 하위.getDescendants();

        // then
        assertThat(최상위_descendants)
                .containsExactlyInAnyOrder(하위, 더하위1, 더하위2, 더더하위1);
        assertThat(하위_descendants)
                .containsExactlyInAnyOrder(더하위1, 더하위2, 더더하위1);
    }
}
