package com.mallang.blog.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.mallang.blog.exception.AlreadyExistAboutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("소개 검증기(AboutValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AboutValidatorTest {

    private final Blog blog1 = mock(Blog.class);
    private final Blog blog2 = mock(Blog.class);
    private final AboutRepository aboutRepository = mock(AboutRepository.class);
    private final AboutValidator aboutValidator = new AboutValidator(aboutRepository);

    @Test
    void 블로그에는_하나의_About만_존재해야_한다() {
        // given
        given(aboutRepository.existsByBlog(blog1))
                .willReturn(true);
        given(aboutRepository.existsByBlog(blog2))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> {
            aboutValidator.validateAlreadyExist(blog1);
        }).isInstanceOf(AlreadyExistAboutException.class);
        assertDoesNotThrow(() -> {
            aboutValidator.validateAlreadyExist(blog2);
        });
    }
}
