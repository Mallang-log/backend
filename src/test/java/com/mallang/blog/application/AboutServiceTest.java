package com.mallang.blog.application;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.MemberRepository;
import com.mallang.blog.AboutFixture;
import com.mallang.blog.application.command.DeleteAboutCommand;
import com.mallang.blog.application.command.UpdateAboutCommand;
import com.mallang.blog.application.command.WriteAboutCommand;
import com.mallang.blog.domain.About;
import com.mallang.blog.domain.AboutRepository;
import com.mallang.blog.domain.AboutValidator;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.domain.BlogRepository;
import com.mallang.blog.exception.AlreadyExistAboutException;
import com.mallang.blog.exception.NoAuthorityAboutException;
import com.mallang.blog.exception.NoAuthorityBlogException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("소개 서비스 (AboutService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AboutServiceTest {

    private final BlogRepository blogRepository = mock(BlogRepository.class);
    private final AboutRepository aboutRepository = mock(AboutRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final AboutValidator aboutValidator = mock(AboutValidator.class);
    private final AboutService aboutService = new AboutService(
            blogRepository,
            aboutRepository,
            memberRepository,
            aboutValidator
    );

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Blog blog = mallangBlog(1L, member);
    private final Long aboutId = 1L;
    private final About about = AboutFixture.about(aboutId, blog);


    @BeforeEach
    void setUp() {
        given(memberRepository.getById(member.getId())).willReturn(member);
        given(memberRepository.getById(other.getId())).willReturn(other);
        given(blogRepository.getByName(blog.getName())).willReturn(blog);
    }

    @Nested
    class 소개_작성_시 {

        private final WriteAboutCommand writeAboutCommand =
                new WriteAboutCommand(member.getId(), blog.getName(), "안녕하세요");

        @Test
        void 첫_작성이라면_작성된다() {
            // given
            given(aboutRepository.save(any()))
                    .willReturn(about);

            // when & then
            assertDoesNotThrow(() -> {
                aboutService.write(writeAboutCommand);
            });
        }

        @Test
        void 블로그에_이미_작성된_소개가_있으면_예외() {
            // given
            willThrow(AlreadyExistAboutException.class)
                    .given(aboutValidator)
                    .validateAlreadyExist(blog);

            // when & then
            assertThatThrownBy(() ->
                    aboutService.write(writeAboutCommand)
            ).isInstanceOf(AlreadyExistAboutException.class);
        }

        @Test
        void 타인의_블로그에_작서하려는_경우_예외() {
            // given
            var command = new WriteAboutCommand(other.getId(), blog.getName(), "안녕하세요");

            // when & then
            assertThatThrownBy(() ->
                    aboutService.write(command)
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }

    @Nested
    class 소개_수정_시 {

        @BeforeEach
        void setUp() {
            given(aboutRepository.getById(aboutId)).willReturn(about);
        }

        @Test
        void 자신의_소개라면_수정된다() {
            // given
            var command = new UpdateAboutCommand(aboutId, member.getId(), "수정");

            // when
            aboutService.update(command);

            // then
            assertThat(about.getContent()).isEqualTo("수정");
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // given
            var command = new UpdateAboutCommand(aboutId, other.getId(), "수정");

            // when & then
            assertThatThrownBy(() -> {
                aboutService.update(command);
            }).isInstanceOf(NoAuthorityAboutException.class);
        }
    }

    @Nested
    class 소개_삭제_시 {

        private final Long aboutId = 1L;
        private final About about = AboutFixture.about(aboutId, blog);

        @BeforeEach
        void setUp() {
            given(aboutRepository.getById(aboutId)).willReturn(about);
        }

        @Test
        void 자신의_소개라면_삭제된다() {
            // given
            var command = new DeleteAboutCommand(aboutId, member.getId());

            // when
            aboutService.delete(command);

            // then
            then(aboutRepository)
                    .should(times(1))
                    .delete(about);
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // given
            var command = new DeleteAboutCommand(aboutId, other.getId());

            // when & then
            assertThatThrownBy(() -> {
                aboutService.delete(command);
            }).isInstanceOf(NoAuthorityAboutException.class);
        }
    }
}
