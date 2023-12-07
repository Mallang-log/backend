package com.mallang.category.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.category.CategoryFixture.하위_카테고리;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityCategoryException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리 (Category) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryTest {

    private final CategoryValidator categoryValidator = mock(CategoryValidator.class);
    private final Member mallang = 깃허브_말랑(1L);
    private final Member otherMember = 깃허브_동훈(2L);
    private final Blog mallangBlog = new Blog("mallang-log", mallang);
    private final Blog otherBlog = new Blog("other-log", otherMember);

    @Nested
    class 생성_시 {

        @Test
        void 생성한다() {
            // when & then
            assertDoesNotThrow(() -> {
                new Category("최상위", mallang, mallangBlog);
            });
        }

        @Test
        void 다른_사람의_블로그에_카테고리_생성_시도_시_예외() {
            // when & then
            assertThatThrownBy(() -> {
                new Category("카테고리", mallang, otherBlog);
            }).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Nested
    class 이름_수정_시 {

        private final Category rootCategory = 루트_카테고리("루트", mallang, mallangBlog);

        @Test
        void 이름을_변경할_수_있다() {
            // when
            rootCategory.updateName("말랑", categoryValidator);

            // then
            assertThat(rootCategory.getName()).isEqualTo("말랑");
        }

        @Test
        void 형제_중_이름이_같은게_있다면_예외() {
            // given
            willThrow(DuplicateCategoryNameException.class)
                    .given(categoryValidator)
                    .validateDuplicateNameInSibling(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                rootCategory.updateName("말랑", categoryValidator);
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 카테고리_게층_구조_변경_시 {

        @Test
        void 계층구조를_변경한다() {
            // given
            Category root1 = new Category("root1", mallang, mallangBlog);
            Category root2 = new Category("root2", mallang, mallangBlog);
            Category root1First = new Category("first", mallang, mallangBlog);
            Category root1Second = new Category("second", mallang, mallangBlog);
            Category root1Third = new Category("third", mallang, mallangBlog);
            Category root1Forth = new Category("forth", mallang, mallangBlog);
            root2.updateHierarchy(null, root1, null, categoryValidator);
            root1First.updateHierarchy(root1, null, null, categoryValidator);
            root1Second.updateHierarchy(root1, root1First, null, categoryValidator);
            root1Third.updateHierarchy(root1, root1Second, null, categoryValidator);
            root1Forth.updateHierarchy(root1, root1Third, null, categoryValidator);

            // when
            root2.updateHierarchy(root1, root1Second, root1Third, categoryValidator);

            // then
            assertThat(root1.getChildren()).containsExactlyInAnyOrder(
                    root1First, root1Second, root2, root1Third, root1Forth
            );
            assertThat(root1First.getPreviousSibling()).isNull();
            assertThat(root1First.getNextSibling()).isEqualTo(root1Second);

            assertThat(root1Second.getPreviousSibling()).isEqualTo(root1First);
            assertThat(root1Second.getNextSibling()).isEqualTo(root2);

            assertThat(root2.getPreviousSibling()).isEqualTo(root1Second);
            assertThat(root2.getNextSibling()).isEqualTo(root1Third);

            assertThat(root1Third.getPreviousSibling()).isEqualTo(root2);
            assertThat(root1Third.getNextSibling()).isEqualTo(root1Forth);

            assertThat(root1Forth.getPreviousSibling()).isEqualTo(root1Third);
            assertThat(root1Forth.getNextSibling()).isNull();
        }

        @Test
        void 무한_Depth_가_가능하다() {
            // given
            Category root = new Category("rootCategory", mallang, mallangBlog);
            Category child = new Category("child", mallang, mallangBlog);
            Category childChild = new Category("childChild", mallang, mallangBlog);
            Category childChildChild = new Category("childChildChild", mallang, mallangBlog);

            // when
            child.updateHierarchy(root, null, null, categoryValidator);
            childChild.updateHierarchy(child, null, null, categoryValidator);
            childChildChild.updateHierarchy(childChild, null, null, categoryValidator);

            // then
            assertThat(child.getDescendants()).containsExactly(childChild, childChildChild);
            assertThat(childChild.getDescendants()).containsExactly(childChildChild);
        }

        @Test
        void 변경_이후에도_카테고리의_자식들은_동일하다() {
            // given
            Category root = new Category("Spring", mallang, mallangBlog);

            Category firstChild = new Category("First", mallang, mallangBlog);
            firstChild.updateHierarchy(root, null, null, categoryValidator);

            Category firstFirstChild = new Category("FirstFirst", mallang, mallangBlog);
            firstFirstChild.updateHierarchy(firstChild, null, null, categoryValidator);

            Category secondChild = new Category("Second", mallang, mallangBlog);
            secondChild.updateHierarchy(root, firstChild, null, categoryValidator);

            firstChild.updateHierarchy(null, root, null, categoryValidator);

            // then
            assertThat(root.getDescendants()).containsExactly(secondChild);
            assertThat(firstChild.getParent()).isNull();
            assertThat(firstChild.getDescendants()).containsExactly(firstFirstChild);
        }

        @Test
        void 검증_시_문제가_있다면_예외() {
            // given
            Category root = new Category("root", mallang, mallangBlog);
            Category first = new Category("first", mallang, mallangBlog);
            first.updateHierarchy(root, null, null, categoryValidator);

            willThrow(CategoryHierarchyViolationException.class)
                    .given(categoryValidator)
                    .validateUpdateHierarchy(first, null, null, null);

            // when & then
            assertThatThrownBy(() -> {
                first.updateHierarchy(null, null, null, categoryValidator);
            }).isInstanceOf(CategoryHierarchyViolationException.class);
        }
    }

    @Nested
    class 제거_시 {

        private final Category rootCategory = 루트_카테고리("루트", mallang, mallangBlog);
        private final Category childCategory = 하위_카테고리("하위", mallang, mallangBlog, rootCategory);

        @Test
        void 하위_카테고리가_존재하면_제거할_수_없다() {
            // when & then
            assertThatThrownBy(() ->
                    rootCategory.delete()
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 부모_카테고리의_하위_카테고리에서도_제거된다() {
            // when
            childCategory.delete();

            // then
            assertThat(rootCategory.getSortedChildren()).isEmpty();
        }

        @Test
        void 제거_이벤트가_발핼된다() {
            // when
            childCategory.delete();

            // then
            assertThat(childCategory.domainEvents().get(0))
                    .isInstanceOf(CategoryDeletedEvent.class);
        }
    }

    @Test
    void 주인을_검증한다() {
        // given
        Category 최상위 = new Category("최상위", mallang, mallangBlog);

        // when & then
        assertDoesNotThrow(() -> {
            최상위.validateOwner(mallang);

        });
        assertThatThrownBy(() -> {
            최상위.validateOwner(otherMember);
        }).isInstanceOf(NoAuthorityCategoryException.class);
    }

    @Test
    void 모든_자손을_반환한다() {
        // given
        Category 최상위 = 루트_카테고리("최상위", mallang, mallangBlog);
        Category 하위 = 하위_카테고리("하위", mallang, mallangBlog, 최상위);
        Category 더하위1 = 하위_카테고리("더하위1", mallang, mallangBlog, 하위);
        Category 더하위2 = 하위_카테고리("더하위2", mallang, mallangBlog, 하위, 더하위1, null);
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
