package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.post.exception.BadTagContentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("태그(Tag) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class TagTest {

    private final Post post = Post.builder().build();

    @ParameterizedTest
    @NullAndEmptySource
    void 이름이_null_이거나_공백이면_안된다(String content) {
        // when & then
        assertThatThrownBy(() ->
                new Tag(content, post)
        ).isInstanceOf(BadTagContentException.class);
    }

    @Test
    void 이름이_30_글자_초과이면_안된다() {
        // given
        String over30Content = "말".repeat(31);

        // when & then
        assertThatThrownBy(() ->
                new Tag(over30Content, post)
        ).isInstanceOf(BadTagContentException.class);
    }
}
