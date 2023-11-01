package com.mallang.category.domain;

import static com.mallang.category.CategoryFixture.루트_카테고리;
import static com.mallang.member.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.blog.domain.Blog;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.member.domain.Member;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("카테고리 검증기(CategoryValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class CategoryValidatorTest {

    private final CategoryRepository categoryRepository = mock(CategoryRepository.class);
    private final CategoryValidator categoryValidator = new CategoryValidator(categoryRepository);

    @Test
    void 한_사용자의_루트_카테고리끼리_이름이_겹치면_예외() {
        // given
        Member member = 말랑(1L);
        Blog mallangBlog = new Blog("mallang-log", member);
        Long memberId = member.getId();
        Category spring = 루트_카테고리("Spring", member, mallangBlog);
        Category nodejs = 루트_카테고리("nodejs", member, mallangBlog);
        given(categoryRepository.findAllRootByMemberId(memberId))
                .willReturn(List.of(spring, nodejs));

        // when & then
        assertThatThrownBy(() ->
                categoryValidator.validateDuplicateRootName(memberId, "Spring")
        ).isInstanceOf(DuplicateCategoryNameException.class);
        assertThatThrownBy(() ->
                categoryValidator.validateDuplicateRootName(memberId, "nodejs")
        ).isInstanceOf(DuplicateCategoryNameException.class);
        assertDoesNotThrow(() -> {
            categoryValidator.validateDuplicateRootName(memberId, "mallang");
        });
    }
}
