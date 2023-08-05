package com.mallang.category.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.exception.CategoryHierarchyViolationException;
import com.mallang.category.exception.NoAuthorityUseCategoryException;
import com.mallang.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("카테고리(Category) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CategoryTest {

    @Test
    void 하위_카테고리로_만든다() {
        // given
        Member member1 = memberBuilder().id(1L).nickname("mallang").build();
        Category 최상위 = Category.builder()
                .name("최상위")
                .member(member1)
                .build();
        Category 하위 = Category.builder()
                .name("하위")
                .member(member1)
                .build();

        // when
        하위.setParent(최상위);

        // then
        assertThat(하위.getParent()).isEqualTo(최상위);
    }

    @Test
    void 하위_카테고리를_생성한_회원은_상위_카테고리를_생성한_회원과_같아야한다() {
        // given
        Member member1 = memberBuilder().id(1L).nickname("mallang").build();
        Category 최상위 = Category.builder()
                .name("최상위")
                .member(member1)
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
        Member member1 = memberBuilder().id(1L).nickname("mallang").build();
        Category 최상위 = Category.builder()
                .name("최상위")
                .member(member1)
                .build();
        Category 하위 = Category.builder()
                .name("하위")
                .member(member1)
                .build();
        Category 더하위 = Category.builder()
                .name("더하위")
                .member(member1)
                .build();
        하위.setParent(최상위);

        // when
        더하위.setParent(하위);

        // then
        assertThat(하위.getParent()).isEqualTo(최상위);
    }

    @Test
    void 자신보다_낮은_Category_를_부모로_둘_수_없다() {
        Member member1 = memberBuilder().id(1L).nickname("mallang").build();
        Category 최상위 = Category.builder()
                .name("최상위")
                .member(member1)
                .build();
        Category 하위 = Category.builder()
                .name("하위")
                .member(member1)
                .build();
        Category 더하위 = Category.builder()
                .name("더하위")
                .member(member1)
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
}
