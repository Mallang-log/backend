package com.mallang.common.utils;

import static com.mallang.common.utils.ObjectsUtils.validateWhenNonNullWithFailCond;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("객체 유틸리티 클래스 (ObjectsUtils) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ObjectsUtilsTest {

    @Nested
    class validateWhenNonNullWithFailCond_test {

        @Test
        void 주어진_인자가_null_이면_통과() {
            // when & then
            assertDoesNotThrow(() -> {
                validateWhenNonNullWithFailCond(
                        null,
                        obj -> true,
                        new RuntimeException()
                );
            });
        }

        @Test
        void 주어진_인자가_null_이_아닐_때_실패_조건과_일치하지_않으면_통과() {
            // when & then
            assertDoesNotThrow(() -> {
                validateWhenNonNullWithFailCond(
                        "nonNull",
                        obj -> false,
                        new RuntimeException()
                );
            });
        }

        @Test
        void 주어진_인자가_null_이_아닐_때_실패_조건과_일치하면_특정_예외를_발생시킨다() {
            // when & then
            assertThatThrownBy(() -> {
                validateWhenNonNullWithFailCond(
                        "nonNull",
                        obj -> true,
                        new RuntimeException()
                );
            });
        }
    }

    @Nested
    class isNulls_test {

        @Test
        void 객체가_주어지지_않으면_true() {
            // when
            boolean nulls = ObjectsUtils.isNulls();

            // then
            assertThat(nulls).isTrue();
        }

        @Test
        void 주어진_모든_객체가_null_이면_true() {
            // given
            Object a = null;
            Object b = null;
            Object c = null;

            // when
            boolean nulls = ObjectsUtils.isNulls(a, b, c);

            // then
            assertThat(nulls).isTrue();
        }

        @Test
        void 주어진_겍체_중_하나라도_null_이_아니라면_false() {
            // given
            Object a = null;
            Object b = "123";
            Object c = null;

            // when
            boolean nulls = ObjectsUtils.isNulls(a, b, c);

            // then
            assertThat(nulls).isFalse();
        }
    }

    @Nested
    class notEquals_test {

        @Test
        void 둘_다_null_이면_false() {
            // when
            boolean notEquals = ObjectsUtils.notEquals(null, null);

            // then
            assertThat(notEquals).isFalse();
        }

        @Test
        void 두_값이_같으면_false() {
            // given
            int a = 10;
            int b = 10;

            // when
            boolean notEquals = ObjectsUtils.notEquals(a, b);

            // then
            assertThat(notEquals).isFalse();
        }

        @Test
        void 두_값이_다르면_true() {
            // given
            int a = 10;
            int b = 11;

            // when
            boolean notEquals = ObjectsUtils.notEquals(a, b);

            // then
            assertThat(notEquals).isTrue();
        }
    }
}
