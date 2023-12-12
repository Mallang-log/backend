package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.reference.exception.InvalidLabelColorException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("라벨 색상 (LabelColor) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class LabelColorTest {

    @Test
    void RGB_색상_코드를_받아_생성된다() {
        // given
        String code = "#aaaaaa";

        // when & then
        assertDoesNotThrow(() -> {
            new LabelColor(code);
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 색상_코드가_없다면_예외(String nullAndEmpty) {
        // when
        assertThatThrownBy(() -> {
            new LabelColor(nullAndEmpty);
        }).isInstanceOf(InvalidLabelColorException.class)
                .hasMessage("라벨 색상은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "#AAA",
            "AAAAAA",
            "#GGGGGG"
    })
    void 색상_코드의_형식이_올바르지_않다면_예외(String invalid) {
        // when
        assertThatThrownBy(() -> {
            new LabelColor(invalid);
        }).isInstanceOf(InvalidLabelColorException.class)
                .hasMessage("라벨 색상은 #으로 시작해야 하며, 3개의 16진수 (6자리)로 이루어져야 합니다. (예시: #AAAAAA)");
    }
}
