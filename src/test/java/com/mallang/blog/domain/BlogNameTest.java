package com.mallang.blog.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.blog.exception.BlogNameException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
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
    void 이름이_4자_미만이거나_32자_초과이면_예외(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "correct-domain-name-1234",
            "2-it-is-also-right-do_main-1"
    })
    void 영문_소문자_숫자_하이픈_언더바로만_구성되어야_한다(String name) {
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
    void 영문_대문자_한글_이모지_언더바_등이_들어오면_예외(String name) {
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

    @ParameterizedTest
    @ValueSource(strings = {
            "correct__name",
    })
    void 언더바는_연속해서_사용할_수_있다(String name) {
        // when & then
        assertDoesNotThrow(() -> {
            new BlogName(name);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "_wrong",
            "wrong_",
    })
    void 언더바로_시작하거나_끝나서는_안된다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "wr ong",
    })
    void 공백은_올_수_없다(String name) {
        // when & then
        assertThatThrownBy(() ->
                new BlogName(name)
        ).isInstanceOf(BlogNameException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            " correct",
            " correct ",
            "correct ",
            "correct     ",
            "       correct",
            "       correct      ",
    })
    void 앞뒤로_존재하는_여러개의_공백은_모두_제거된다(String name) {
        // when
        BlogName blogName = new BlogName(name);

        // then
        assertThat(blogName.getValue()).isEqualTo("correct");
    }

    @Test
    void 이름이_같으면_같다고_판단한다() {
        // given
        BlogName blogName = new BlogName("test");
        BlogName same = new BlogName("test");
        BlogName otherName = new BlogName("ttttt");

        // when & then
        assertThat(blogName.equals(blogName)).isTrue();
        assertThat(blogName.equals(same)).isTrue();
        assertThat(blogName.equals("test")).isFalse();
        assertThat(blogName.equals(otherName)).isFalse();
        assertThat(blogName.hashCode()).isEqualTo(same.hashCode());
    }
}
