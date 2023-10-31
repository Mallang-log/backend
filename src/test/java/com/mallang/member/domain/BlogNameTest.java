package com.mallang.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.blog.domain.BlogName;
import com.mallang.blog.exception.BlogNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("블로그 이름(BlogName) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogNameTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "aaaa",
            "01234567890123456789012345678901"
    })
    void 최소_4자_최대_32자_이내여야_한다(String name) {
        // when & then
        assertDoesNotThrow(() -> {
            new BlogName(name);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "aaa",
            "012345678901234567890123456789012"
    })
    void 이름이_4자_미만이거나_32자_초과이면_예외이다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "correct-domain-name-1234",
            "2-it-is-also-right-domain-1"
    })
    void 영문_소문자_숫자_하이픈으로만_구성되어야_한다(String name) {
        // when & then
        assertDoesNotThrow(() -> {
            new BlogName(name);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wrong-이름",
            "it-is-wrong-👍"
    })
    void 영문_대문자_한글_이모지_언더바_등이_들어오면_예외이다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wrong--이름",
    })
    void 하이폰은_연속해서_사용할_수_없다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-wrong-이름",
            "wrong-이름-",
    })
    void 하이폰으로_시작하거나_끝나서는_안된다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }
}