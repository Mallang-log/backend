package com.mallang.blog.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.blog.exception.BlogNameException;
import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.blog.exception.TooManyBlogsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 (Blog) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogTest {

    private final BlogValidator blogValidator = mock(BlogValidator.class);
    private final Member owner = 깃허브_말랑(1L);
    private final Blog blog = mallangBlog(owner);

    @Nested
    class 개설_시 {

        @Test
        void 블로그_이름에_문제가_있으면_예외() {
            // given
            String blogName = "aaa";

            // when & then
            assertThatThrownBy(() -> {
                new Blog(blogName, owner);
            }).isInstanceOf(BlogNameException.class);
        }

        @Test
        void 블로그를_생성하려는_회원이_이미_다른_블로그를_가지고_있으면_예외() {
            // given
            willThrow(TooManyBlogsException.class)
                    .given(blogValidator)
                    .validateOpen(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                blog.open(blogValidator);
            }).isInstanceOf(TooManyBlogsException.class);
        }

        @Test
        void 중복된_이름을_가진_다른_블로그가_존재하면_예외() {
            // given
            willThrow(DuplicateBlogNameException.class)
                    .given(blogValidator)
                    .validateOpen(any(), any());

            // when & then
            assertThatThrownBy(() -> {
                blog.open(blogValidator);
            }).isInstanceOf(DuplicateBlogNameException.class);
        }

        @Test
        void 문제_없는경우_개설된다() {
            // given
            willDoNothing()
                    .given(blogValidator)
                    .validateOpen(any(), any());

            // when & then
            assertDoesNotThrow(() -> {
                blog.open(blogValidator);
            });
        }
    }

    @Test
    void 주인_검증() {
        // given
        Member other = 깃허브_동훈(2L);

        // when & then
        assertDoesNotThrow(() -> {
            blog.validateOwner(owner);
        });
        assertThatThrownBy(() -> {
            blog.validateOwner(other);
        }).isInstanceOf(NoAuthorityBlogException.class);
    }
}
