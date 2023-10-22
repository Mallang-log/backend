package com.mallang.category.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.category.exception.NoAuthorityDeleteCategoryException;
import com.mallang.category.exception.NoAuthorityUpdateCategoryException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.member.MemberFixture;
import com.mallang.member.domain.Member;
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
    private final Member member = MemberFixture.회원(1L, "mallang");

    @Nested
    class 생성_시 {

        @Test
        void 하위_카테고리로_만든다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);

            // when
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
        }

        @Test
        void 하위_카테고리를_생성한_회원은_상위_카테고리를_생성한_회원과_같아야한다() {
            // given
            Member member2 = MemberFixture.회원(2L, "mallang");
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);

            // when
            assertThatThrownBy(() ->
                    Category.create("하위", member2, 최상위, categoryValidator)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            assertThat(최상위.getChildren()).isEmpty();
        }

        @Test
        void 무한_Depth_가_가능하다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);

            // when
            Category 더하위 = Category.create("더하위", member, 하위, categoryValidator);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
            assertThat(더하위.getParent()).isEqualTo(하위);
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    Category.create("하위", member, 최상위, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category 최상위1 = Category.create("최상위", member, null, categoryValidator);
            willThrow(new DuplicateCategoryNameException())
                    .given(categoryValidator)
                    .validateDuplicateRootName(member.getId(), "최상위");

            // when & then
            assertThatThrownBy(() ->
                    Category.create("최상위", member, null, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 자기_자신이_부모여서는_안된다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    최상위.update(member.getId(), "name", 최상위, categoryValidator)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자신보다_낮은_Category_를_부모로_둘_수_없다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    최상위.update(member.getId(), "name", 하위, categoryValidator)
            ).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 이름을_변경할_수_있다() {
            // given
            Category category = Category.create("최상위", member, null, categoryValidator);

            // when
            category.update(member.getId(), "말랑", null, categoryValidator);

            // then
            assertThat(category.getName()).isEqualTo("말랑");
        }

        @Test
        void 자신의_카테고리가_아니라면_수정할_수_없다() {
            // given
            Member member2 = MemberFixture.회원(2L, "dong");
            Category category = Category.create("최상위", member, null, categoryValidator);

            // when
            assertThatThrownBy(() ->
                    category.update(member2.getId(), "말랑", null, categoryValidator)
            ).isInstanceOf(NoAuthorityUpdateCategoryException.class);

            // then
            assertThat(category.getName()).isEqualTo("최상위");
        }

        @Test
        void 같은_부모를_가진_직계_자식끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);
            Category 하위1 = Category.create("하위1", member, 최상위, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    하위1.update(member.getId(), "하위", 최상위, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 루트끼리는_이름이_겹쳐서는_안된다() {
            // given
            Category 최상위 = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, 최상위, categoryValidator);
            willThrow(new DuplicateCategoryNameException())
                    .given(categoryValidator)
                    .validateDuplicateRootName(member.getId(), "최상위");

            // when & then
            assertThatThrownBy(() ->
                    하위.update(member.getId(), "최상위", null, categoryValidator)
            ).isInstanceOf(DuplicateCategoryNameException.class);
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 자신의_카테고리가_아니면_제거할_수_없다() {
            // given
            Member member2 = MemberFixture.회원(2L, "dong");
            Category category = Category.create("최상위", member, null, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    category.delete(member2.getId())
            ).isInstanceOf(NoAuthorityDeleteCategoryException.class);
        }

        @Test
        void 하위_카테고리가_존재하면_제거할_수_없다() {
            // given
            Category category = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, category, categoryValidator);

            // when & then
            assertThatThrownBy(() ->
                    category.delete(member.getId())
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 부모_카테고리의_하위_카테고리에서도_제거된다() {
            // given
            Category category = Category.create("최상위", member, null, categoryValidator);
            Category 하위 = Category.create("하위", member, category, categoryValidator);

            // when
            하위.delete(member.getId());

            // then
            assertThat(category.getChildren()).isEmpty();
        }

        @Test
        void 제거_이벤트가_발핼된다() {
            // given
            Category category = Category.create("최상위", member, null, categoryValidator);

            // when
            category.delete(member.getId());

            // then
            assertThat(category.domainEvents().get(0))
                    .isInstanceOf(CategoryDeletedEvent.class);
        }
    }

    @Test
    void 모든_자손을_반환한다() {
        // given
        Category 최상위 = Category.create("최상위", member, null, categoryValidator);
        Category 하위 = Category.create("하위", member, 최상위, categoryValidator);
        Category 더하위1 = Category.create("더하위1", member, 하위, categoryValidator);
        Category 더하위2 = Category.create("더하위2", member, 하위, categoryValidator);
        Category 더더하위1 = Category.create("더더하위1", member, 더하위1, categoryValidator);

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
