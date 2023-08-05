package com.mallang.category.domain;

import static com.mallang.member.MemberFixture.memberBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.category.application.exception.NoAuthorityUseCategory;
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
        ).isInstanceOf(NoAuthorityUseCategory.class);

        // then
        assertThat(하위.getParent()).isNull();
    }
}
