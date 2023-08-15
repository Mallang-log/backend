package com.mallang.category.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.category.application.CategoryServiceTestHelper;
import com.mallang.category.exception.DuplicateCategoryNameException;
import com.mallang.member.MemberServiceTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@DisplayName("카테고리 검증기(CategoryValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class CategoryValidatorTest {

    @Autowired
    private CategoryValidator categoryValidator;

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private CategoryServiceTestHelper categoryServiceTestHelper;

    @Test
    void 한_사용자의_루트_카테고리끼리_이름이_겹치면_예외() {
        // given
        Long 말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
        categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "1");
        categoryServiceTestHelper.최상위_카테고리를_저장한다(말랑_ID, "2");

        // when & then
        assertThatThrownBy(() ->
                categoryValidator.validateDuplicateRootName(말랑_ID, "1")
        ).isInstanceOf(DuplicateCategoryNameException.class);
        assertThatThrownBy(() ->
                categoryValidator.validateDuplicateRootName(말랑_ID, "2")
        ).isInstanceOf(DuplicateCategoryNameException.class);
        assertDoesNotThrow(() ->
                categoryValidator.validateDuplicateRootName(말랑_ID, "3")
        );
    }
}
