package com.mallang.blog.application;

import static com.mallang.auth.MemberFixture.동훈;
import static com.mallang.auth.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.application.command.DeleteAboutCommand;
import com.mallang.blog.application.command.UpdateAboutCommand;
import com.mallang.blog.application.command.WriteAboutCommand;
import com.mallang.blog.domain.About;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.AlreadyExistAboutException;
import com.mallang.blog.exception.NotFoundAboutException;
import com.mallang.blog.exception.NotFoundBlogException;
import com.mallang.common.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("소개 서비스(AboutService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AboutServiceTest extends ServiceTest {

    private Member member;
    private Member other;
    private Blog blog;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(말랑());
        other = memberRepository.save(동훈());
        blog = blogRepository.save(new Blog("mallang-log", member));
    }

    @Nested
    class 소개_작성_시 {

        @Test
        void 첫_작성이라면_작성된다() {
            // given
            WriteAboutCommand command = new WriteAboutCommand(member.getId(), blog.getName(), "안녕하세요");

            // when & then
            assertDoesNotThrow(() -> {
                aboutService.write(command);
            });
        }

        @Test
        void 블로그에_이미_작성된_소개가_있으면_예외() {
            // given
            WriteAboutCommand command = new WriteAboutCommand(member.getId(), blog.getName(), "안녕하세요");
            aboutService.write(command);

            // when & then
            assertThatThrownBy(() ->
                    aboutService.write(command)
            ).isInstanceOf(AlreadyExistAboutException.class);
        }

        @Test
        void 타인의_블로그에_작서하려는_경우_예외() {
            // given
            WriteAboutCommand command = new WriteAboutCommand(other.getId(), blog.getName(), "안녕하세요");

            // when & then
            assertThatThrownBy(() ->
                    aboutService.write(command)
            ).isInstanceOf(NotFoundBlogException.class);
        }
    }

    @Nested
    class 소개_수정_시 {

        private Long aboutId;

        @BeforeEach
        void setUp() {
            WriteAboutCommand command = new WriteAboutCommand(member.getId(), blog.getName(), "안녕하세요");
            aboutId = aboutService.write(command);
        }

        @Test
        void 자신의_소개라면_수정된다() {
            // given
            UpdateAboutCommand command = new UpdateAboutCommand(aboutId, member.getId(), blog.getName(), "수정");

            // when
            aboutService.update(command);

            // then
            About about = aboutRepository.findById(aboutId).get();
            assertThat(about.getContent()).isEqualTo("수정");
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // given
            UpdateAboutCommand command = new UpdateAboutCommand(aboutId, other.getId(), blog.getName(), "수정");

            // when & then
            assertThatThrownBy(() -> {
                aboutService.update(command);
            }).isInstanceOf(NotFoundAboutException.class);
        }
    }

    @Nested
    class 소개_삭제_시 {

        private Long aboutId;

        @BeforeEach
        void setUp() {
            WriteAboutCommand command = new WriteAboutCommand(member.getId(), blog.getName(), "안녕하세요");
            aboutId = aboutService.write(command);
        }

        @Test
        void 자신의_소개라면_삭제된다() {
            // given
            DeleteAboutCommand command = new DeleteAboutCommand(aboutId, member.getId(), blog.getName());

            // when
            aboutService.delete(command);

            // then
            assertThat(aboutRepository.findById(aboutId)).isEmpty();
        }

        @Test
        void 자신의_소개가_아니면_예외() {
            // given
            DeleteAboutCommand command = new DeleteAboutCommand(aboutId, other.getId(), blog.getName());

            // when & then
            assertThatThrownBy(() -> {
                aboutService.delete(command);
            }).isInstanceOf(NotFoundAboutException.class);
        }
    }
}
