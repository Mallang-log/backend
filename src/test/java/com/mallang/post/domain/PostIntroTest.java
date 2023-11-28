package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.post.exception.InvalidPostIntroLengthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("포스트 인트로 (PostIntro) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostIntroTest {

    @Test
    void 길이는_1글자_이상_250_글자_이하여야_한다() {
        // when & then
        assertDoesNotThrow(() -> {
            new PostIntro("1");
        });
        assertDoesNotThrow(() -> {
            new PostIntro("1".repeat(250));
        });
    }

    @ParameterizedTest
    @NullAndEmptySource
    void 길익가_0이거나_null_이라면_예외(String value) {
        // when & then
        assertThatThrownBy(() -> {
            new PostIntro(value);
        }).isInstanceOf(InvalidPostIntroLengthException.class);
    }

    @Test
    void 길익가_250글자_초과라면_예외() {
        // given
        String repeat = "1".repeat(251);

        // when & then
        assertThatThrownBy(() -> {
            new PostIntro(repeat);
        }).isInstanceOf(InvalidPostIntroLengthException.class);
    }

    @Test
    void 내용이_같으면_같다() {
        // given
        PostIntro intro1 = new PostIntro("123");
        PostIntro intro2 = new PostIntro("123");

        // when & then
        assertThat(intro1)
                .isNotEqualTo(new Object())
                .isEqualTo(intro1)
                .isEqualTo(intro2)
                .hasSameHashCodeAs(intro2);
    }

    @Test
    void 앞뒤_공백은_제거된다() {
        // when
        PostIntro intro1 = new PostIntro(" 123 ");
        PostIntro intro2 = new PostIntro("  123  ");

        // then
        assertThat(intro1.getIntro()).isEqualTo("123");
        assertThat(intro2.getIntro()).isEqualTo("123");
        assertThatThrownBy(() -> {
            new PostIntro("  ");
        }).isInstanceOf(InvalidPostIntroLengthException.class);
    }
}
