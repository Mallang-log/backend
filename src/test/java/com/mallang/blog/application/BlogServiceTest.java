package com.mallang.blog.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogName;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.domain.BlogValidator;
import com.mallang.blog.exception.BlogNameException;
import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.TooManyBlogsException;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("블로그 서비스 (BlogService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class BlogServiceTest extends ServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final BlogValidator blogValidator = mock(BlogValidator.class);
    private final BlogService blogService = new BlogService(
            blogRepository,
            memberRepository,
            blogValidator
    );

    private final Long 말랑_ID = 1L;
    private final Member member = 깃허브_말랑(말랑_ID);
    private final Blog blog = mallangBlog(2L, member);

    @Nested
    class 개설_시 {

        @BeforeEach
        void setUp() {
            given(memberRepository.getById(말랑_ID))
                    .willReturn(member);
        }

        @Test
        void 블로그를_생성하려는_회원이_이미_다른_블로그를_가지고_있으면_예외() {
            // given
            var command = new OpenBlogCommand(말랑_ID, "mallang-blog");
            willThrow(TooManyBlogsException.class)
                    .given(blogValidator)
                    .validateOpen(member.getId(), new BlogName("mallang-blog"));

            // when & then
            assertThatThrownBy(() -> {
                blogService.open(command);
            }).isInstanceOf(TooManyBlogsException.class);
        }

        @Test
        void 중복된_이름을_가진_다른_블로그가_존재하면_예외() {
            // given
            var command = new OpenBlogCommand(말랑_ID, "mallang-blog");
            willThrow(DuplicateBlogNameException.class)
                    .given(blogValidator)
                    .validateOpen(member.getId(), new BlogName("mallang-blog"));

            // when & then
            assertThatThrownBy(() -> {
                blogService.open(command);
            }).isInstanceOf(DuplicateBlogNameException.class);
        }

        @Test
        void 블로그_이름이_규칙에_맞지_않으면_예외() {
            // given
            var invalidName = "invalid--name";

            // when & then
            assertThatThrownBy(() ->
                    blogService.open(new OpenBlogCommand(말랑_ID, invalidName))
            ).isInstanceOf(BlogNameException.class);
        }

        @Test
        void 문제_없는경우_개설된다() {
            // given
            given(blogRepository.save(any()))
                    .willReturn(blog);
            var command = new OpenBlogCommand(말랑_ID, "mallang-blog");

            // when
            Long id = blogService.open(command);

            // then
            assertThat(id).isNotNull();
        }
    }
}
