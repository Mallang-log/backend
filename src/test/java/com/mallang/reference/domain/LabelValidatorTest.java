package com.mallang.reference.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.category.CategoryHierarchyViolationException;
import com.mallang.category.DuplicateCategoryNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("라벨 검증기 (LabelValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LabelValidatorTest {

    private final Member member = 깃허브_말랑(1L);
    private final LabelRepository labelRepository = mock(LabelRepository.class);
    private final LabelValidator labelValidator = new LabelValidator(labelRepository);

    @Nested
    class 회원의_라벨_존재_여부_검증_시 {

        @Test
        void 존재하면_예외() {
            // given
            given(labelRepository.existsByOwner(member))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> {
                labelValidator.validateNoCategories(member);
            }).isInstanceOf(CategoryHierarchyViolationException.class);
        }

        @Test
        void 존재하지_않으면_통과() {
            // given
            given(labelRepository.existsByOwner(member))
                    .willReturn(false);

            // when & then
            assertDoesNotThrow(() -> {
                labelValidator.validateNoCategories(member);
            });
        }
    }

    @Nested
    class 회원의_라벨_중_중복_네임_검사_시 {

        @Test
        void 중복_이름이_존재하면_예외() {
            // given
            String duplicated = "duplicated";
            given(labelRepository.existsByOwnerAndName(member, duplicated))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> {
                labelValidator.validateDuplicateName(member, duplicated);
            }).isInstanceOf(DuplicateCategoryNameException.class);
        }

        @Test
        void 중복_이름이_존재하지_않으면_통과() {
            // given
            String nonDuplicated = "nonDuplicated";
            given(labelRepository.existsByOwnerAndName(member, nonDuplicated))
                    .willReturn(false);
            // when & then
            assertDoesNotThrow(() -> {
                labelValidator.validateDuplicateName(member, nonDuplicated);
            });
        }
    }
}
