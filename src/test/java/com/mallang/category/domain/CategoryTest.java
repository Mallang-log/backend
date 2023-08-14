package com.mallang.category.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.ChildCategoryExistException;
import com.mallang.category.exception.NoAuthorityDeleteCategoryException;
import com.mallang.category.exception.NoAuthorityUpdateCategoryException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.member.MemberFixture;
import com.mallang.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("카테고리(Category) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryTest {

    private final Member member = MemberFixture.회원(1L, "mallang");

    @Nested
    class 부모_설정_시 {

        @Test
        void 하위_카테고리로_만든다() {
            // given
            Category 최상위 = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Category 하위 = Category.builder()
                    .name("하위")
                    .member(member)
                    .build();

            // when
            하위.setParent(최상위);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
        }

        @Test
        void 하위_카테고리를_생성한_회원은_상위_카테고리를_생성한_회원과_같아야한다() {
            // given
            Category 최상위 = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Member member2 = memberBuilder().id(2L).nickname("mallang").build();
            Category 하위 = Category.builder()
                    .name("하위")
                    .member(member2)
                    .build();

            // when
            assertThatThrownBy(() ->
                    하위.setParent(최상위)
            ).isInstanceOf(NoAuthorityUseCategoryException.class);

            // then
            assertThat(하위.getParent()).isNull();
        }

        @Test
        void 무한_Depth_가_가능하다() {
            // given
            Category 최상위 = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Category 하위 = Category.builder()
                    .name("하위")
                    .member(member)
                    .build();
            Category 더하위 = Category.builder()
                    .name("더하위")
                    .member(member)
                    .build();
            하위.setParent(최상위);

            // when
            더하위.setParent(하위);

            // then
            assertThat(하위.getParent()).isEqualTo(최상위);
        }

        @Test
        void 자신보다_낮은_Category_를_부모로_둘_수_없다() {
            // given
            Category 최상위 = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Category 하위 = Category.builder()
                    .name("하위")
                    .member(member)
                    .build();
            Category 더하위 = Category.builder()
                    .name("더하위")
                    .member(member)
                    .build();
            하위.setParent(최상위);
            더하위.setParent(하위);

            // when & then
            assertThatThrownBy(() -> 최상위.setParent(하위))
                    .isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() -> 최상위.setParent(더하위))
                    .isInstanceOf(CategoryHierarchyViolationException.class);
            assertThatThrownBy(() -> 하위.setParent(더하위))
                    .isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 자기_자신이_부모여서는_안된다() {
            // given
            Category 최상위 = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();

            // when & then
            assertThatThrownBy(() -> 최상위.setParent(최상위))
                    .isInstanceOf(CategoryHierarchyViolationException.class);
        }
    }

    @Nested
    class 수정_시 {

        @Test
        void 이름을_변경할_수_있다() {
            // given
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();

            // when
            category.update(member.getId(), "말랑", null);

            // then
            assertThat(category.getName()).isEqualTo("말랑");
        }

        @Test
        void 자신의_카테고리가_아니라면_수정할_수_없다() {
            // given
            Member member2 = MemberFixture.회원(2L, "dong");
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();

            // when
            assertThatThrownBy(() ->
                    category.update(member2.getId(), "말랑", null)
            ).isInstanceOf(NoAuthorityUpdateCategoryException.class);

            // then
            assertThat(category.getName()).isEqualTo("최상위");
        }
    }

    @Nested
    class 제거_시 {

        @Test
        void 자신의_카테고리가_아니면_제거할_수_없다() {
            // given
            Member member2 = MemberFixture.회원(2L, "dong");
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();

            // when & then
            assertThatThrownBy(() ->
                    category.delete(member2.getId())
            ).isInstanceOf(NoAuthorityDeleteCategoryException.class);
        }

        @Test
        void 하위_카테고리가_존재하면_제거할_수_없다() {
            // given
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Category childCategory = Category.builder()
                    .name("하위")
                    .member(member)
                    .build();
            childCategory.setParent(category);

            // when & then
            assertThatThrownBy(() ->
                    category.delete(member.getId())
            ).isInstanceOf(ChildCategoryExistException.class);
        }

        @Test
        void 부모_카테고리의_하위_카테고리에서도_제거된다() {
            // given
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();
            Category childCategory = Category.builder()
                    .name("하위")
                    .member(member)
                    .build();
            childCategory.setParent(category);

            // when
            childCategory.delete(member.getId());

            // then
            assertThat(category.getChildren()).isEmpty();
        }

        @Test
        void 제거_이벤트가_발핼된다() {
            // given
            Category category = Category.builder()
                    .name("최상위")
                    .member(member)
                    .build();

            // when
            category.delete(member.getId());

            // then
            assertThat(category.domainEvents().get(0))
                    .isInstanceOf(CategoryDeletedEvent.class);
        }
    }

    @Test
    void 자신과_자신의_하위_카테고리_중_특정_ID가_포함되는지_확인한다() {
        // given
        Category 최상위 = Category.builder()
                .name("최상위")
                .member(member)
                .build();
        ReflectionTestUtils.setField(최상위, "id", 1L);
        Category 하위 = Category.builder()
                .name("하위")
                .member(member)
                .build();
        ReflectionTestUtils.setField(하위, "id", 2L);
        Category 더하위 = Category.builder()
                .name("더하위")
                .member(member)
                .build();
        ReflectionTestUtils.setField(더하위, "id", 3L);
        하위.setParent(최상위);
        더하위.setParent(하위);

        // when & then
        assertThat(더하위.equalIdOrContainsIdInParent(3L)).isTrue();
        assertThat(더하위.equalIdOrContainsIdInParent(2L)).isTrue();
        assertThat(더하위.equalIdOrContainsIdInParent(1L)).isTrue();

        assertThat(하위.equalIdOrContainsIdInParent(3L)).isFalse();
        assertThat(하위.equalIdOrContainsIdInParent(2L)).isTrue();
        assertThat(하위.equalIdOrContainsIdInParent(1L)).isTrue();

        assertThat(최상위.equalIdOrContainsIdInParent(3L)).isFalse();
        assertThat(최상위.equalIdOrContainsIdInParent(2L)).isFalse();
        assertThat(최상위.equalIdOrContainsIdInParent(1L)).isTrue();
    }
}
