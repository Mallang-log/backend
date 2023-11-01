package com.mallang.blog.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.TooManyBlogsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 검증기(BlogValidator) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogValidatorTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final BlogValidator blogValidator = new BlogValidator(blogRepository);

    @Nested
    class 블로그_생성_시 {

        @Test
        void 블로그를_생성하려는_회원이_이미_다른_블로그를_가지고_있는지_검사한다() {
            // given
            given(blogRepository.existsByMemberId(1L))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> {
                blogValidator.validateOpen(1L, new BlogName("mallang"));
            }).isInstanceOf(TooManyBlogsException.class);
        }

        @Test
        void 중복된_다른_이름을_가진_블로그가_있는지_검증한다() {
            // given
            BlogName blogName = new BlogName("mallang");
            given(blogRepository.existsByName(blogName))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> {
                blogValidator.validateOpen(1L, blogName);
            }).isInstanceOf(DuplicateBlogNameException.class);
        }

        @Test
        void 회원의_블로그가_존재하지_않으며_중복된_다른_블로그_이름이_없는_경우_문제없다() {
            // when & then
            assertDoesNotThrow(() -> {
                blogValidator.validateOpen(1L, new BlogName("mallang"));
            });
        }
    }
}
