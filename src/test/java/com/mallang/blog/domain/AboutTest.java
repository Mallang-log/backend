package com.mallang.blog.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.domain.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.exception.AlreadyExistAboutException;
import com.mallang.blog.exception.NoAuthorityAboutException;
import com.mallang.blog.exception.NoAuthorityBlogException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 소개 (About) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AboutTest {

    private final AboutValidator aboutValidator = mock(AboutValidator.class);
    private final Member member = 깃허브_말랑(1L);
    private final Blog blog = mallangBlog(member);

    @Nested
    class 작성_시 {

        @Test
        void 이미_작성된_About_이_있으면_예외() {
            // given
            willThrow(AlreadyExistAboutException.class)
                    .given(aboutValidator)
                    .validateAlreadyExist(blog);
            About about = new About(blog, "안녕하세요", member);

            // when & then
            assertThatThrownBy(() -> {
                about.write(aboutValidator);
            }).isInstanceOf(AlreadyExistAboutException.class);
        }

        @Test
        void 작성된_About_이_없다면_성공() {
            // given
            About about = new About(blog, "안녕하세요", member);

            // when & then
            assertDoesNotThrow(() -> {
                about.write(aboutValidator);
            });
        }

        @Test
        void 다른_사람의_블로그에_생성_시도_시_예외() {
            // given
            Member other = 깃허브_동훈(2L);

            // when & then
            assertThatThrownBy(() ->
                    new About(blog, "안녕하세요", other)
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Test
    void 수정_시_내용을_변경한다() {
        // given
        About about = new About(blog, "안녕하세요", member);

        // when
        about.update("1234");

        // then
        assertThat(about.getContent()).isEqualTo("1234");
    }

    @Test
    void 작성자를_검증한다() {
        // given
        About about = new About(blog, "안녕하세요", member);
        Member other = 깃허브_동훈(2L);

        // when & then
        assertDoesNotThrow(() -> {
            about.validateWriter(member);
        });
        assertThatThrownBy(() -> {
            about.validateWriter(other);
        }).isInstanceOf(NoAuthorityAboutException.class);
    }
}
