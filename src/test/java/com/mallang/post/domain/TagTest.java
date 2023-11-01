package com.mallang.post.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

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

    private final Post post = mock(Post.class);

    @Test
    void 작성될_수_있다() {
        // when
        Tag tag = new Tag("tag", post);

        // then
        assertThat(tag.getContent()).isEqualTo("tag");
        assertThat(tag.getPost()).isEqualTo(post);
    }

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
        String just30Content = "말".repeat(30);

        // when & then
        assertThatThrownBy(() ->
                new Tag(over30Content, post)
        ).isInstanceOf(BadTagContentException.class);
        assertDoesNotThrow(() ->
                new Tag(just30Content, post)
        );
    }
}
