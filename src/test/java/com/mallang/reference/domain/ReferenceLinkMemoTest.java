package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.reference.exception.BadReferenceLinkMemoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("참조 링크의 메모 (ReferenceLinkMemo) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkMemoTest {

    @Nested
    class 생성_시 {

        @ParameterizedTest
        @NullAndEmptySource
        void 메모는_없어도_된다(String nullAndEmptyMemo) {
            // when & then
            assertDoesNotThrow(() ->
                    new ReferenceLinkMemo(nullAndEmptyMemo)
            );
        }

        @Test
        void 메모의_최대_길이는_100글자이다() {
            // when & then
            assertDoesNotThrow(() ->
                    new ReferenceLinkMemo("1".repeat(100))
            );
            assertThatThrownBy(() ->
                    new ReferenceLinkMemo("1".repeat(101))
            ).isInstanceOf(BadReferenceLinkMemoException.class);
        }
    }
}
