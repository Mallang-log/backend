package com.mallang.blog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.blog.application.command.OpenBlogCommand;
import com.mallang.blog.exception.BlogNameException;
import com.mallang.blog.exception.DuplicateBlogNameException;
import com.mallang.blog.exception.TooManyBlogsException;
import com.mallang.common.ServiceTest;
import com.mallang.member.MemberServiceTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("블로그 서비스(BlogService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ServiceTest
class BlogServiceTest {

    @Autowired
    private MemberServiceTestHelper memberServiceTestHelper;

    @Autowired
    private BlogService blogService;

    private Long 말랑_ID;

    @Nested
    class 개설_시 {

        @BeforeEach
        void setUp() {
            말랑_ID = memberServiceTestHelper.회원을_저장한다("말랑");
        }

        @Test
        void 문제_없는경우_개설된다() {
            // when
            Long 말랑블로그_ID = blogService.open(new OpenBlogCommand(말랑_ID, "mallangblog"));

            // then
            assertThat(말랑블로그_ID).isNotNull();
        }

        @Test
        void 블로그를_생성하려는_회원이_이미_다른_블로그를_가지고_있으면_예와() {
            // given
            blogService.open(new OpenBlogCommand(말랑_ID, "mallangblog"));

            // when & then
            assertThatThrownBy(() -> {
                blogService.open(new OpenBlogCommand(말랑_ID, "mallangblog2"));
            }).isInstanceOf(TooManyBlogsException.class);
        }

        @Test
        void 중복된_이름을_가진_다른_블로그가_존재하면_예외() {
            // given
            Long 안말랑_ID = memberServiceTestHelper.회원을_저장한다("안말랑");
            blogService.open(new OpenBlogCommand(말랑_ID, "mallangblog"));

            // when & then
            assertThatThrownBy(() -> {
                blogService.open(new OpenBlogCommand(안말랑_ID, "mallangblog"));
            }).isInstanceOf(DuplicateBlogNameException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "aaaa",
                "01234567890123456789012345678901"
        })
        void 블로그_이름은_최소_4자_최대_32자_이내여야_한다(String name) {
            // when & then
            assertDoesNotThrow(() -> {
                blogService.open(new OpenBlogCommand(말랑_ID, name));
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "aaa",
                "012345678901234567890123456789012"
        })
        void 블로그_이름이_4자_미만이거나_32자_초과이면_예외이다(String name) {
            // when & then
            assertThatThrownBy(() ->
                    blogService.open(new OpenBlogCommand(말랑_ID, name))
            ).isInstanceOf(BlogNameException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "correct-domain-name-1234",
                "2-it-is-also-right-domain-1"
        })
        void 블로그_이름은_영문_소문자_숫자_하이픈으로만_구성되어야_한다(String name) {
            // when & then
            assertDoesNotThrow(() -> {
                blogService.open(new OpenBlogCommand(말랑_ID, name));
            });
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "wrong-이름",
                "it-is-wrong-👍"
        })
        void 블로그_이름에_영문_대문자_한글_이모지_언더바_등이_들어오면_예외이다(String name) {
            // when & then
            assertThatThrownBy(() ->
                    blogService.open(new OpenBlogCommand(말랑_ID, name))
            ).isInstanceOf(BlogNameException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "wrong--이름",
        })
        void 블로그_이름에_하이폰은_연속해서_사용할_수_없다(String name) {
            // when & then
            assertThatThrownBy(() ->
                    blogService.open(new OpenBlogCommand(말랑_ID, name))
            ).isInstanceOf(BlogNameException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "-wrong-이름",
                "wrong-이름-",
        })
        void 블로그_이름은_하이폰으로_시작하거나_끝나서는_안된다(String name) {
            // when & then
            assertThatThrownBy(() ->
                    blogService.open(new OpenBlogCommand(말랑_ID, name))
            ).isInstanceOf(BlogNameException.class);
        }
    }
}
