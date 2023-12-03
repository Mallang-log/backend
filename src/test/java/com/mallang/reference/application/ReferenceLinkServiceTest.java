package com.mallang.reference.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.blog.exception.NoAuthorityBlogException;
import com.mallang.common.ServiceTest;
import com.mallang.reference.application.command.SaveReferenceLinkCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("참고 링크 서비스 (ReferenceLinkService) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkServiceTest extends ServiceTest {

    private Long memberId;
    private String blogName;

    @BeforeEach
    void setUp() {
        memberId = 회원을_저장한다("말랑");
        blogName = 블로그_개설(memberId, "mallang-log");
    }

    @Nested
    class 참조_링크_저장_시 {

        @Test
        void 참조_링크를_저장한다() {
            // given
            SaveReferenceLinkCommand command = new SaveReferenceLinkCommand(
                    memberId,
                    blogName,
                    "https://ttl-blog.tistory.com/",
                    "말랑이 블로그",
                    "짱 멋있는 말랑이 블로그임"
            );

            // when
            Long linkId = referenceLinkService.save(command);

            // then
            assertThat(linkId).isNotNull();
        }

        @Test
        void 중복되는_url_이어도_괜찮다() {
            // given
            SaveReferenceLinkCommand command = new SaveReferenceLinkCommand(
                    memberId,
                    blogName,
                    "https://ttl-blog.tistory.com/",
                    "말랑이 블로그",
                    "짱 멋있는 말랑이 블로그임"
            );
            referenceLinkService.save(command);

            // when
            Long id = referenceLinkService.save(command);

            // then
            assertThat(id).isNotNull();
        }

        @Test
        void 블로그의_주인이_아닌_경우_예외() {
            // given
            Long otherMember = 회원을_저장한다("other");
            SaveReferenceLinkCommand command = new SaveReferenceLinkCommand(
                    otherMember,
                    blogName,
                    "https://ttl-blog.tistory.com/",
                    "말랑이 블로그",
                    "짱 멋있는 말랑이 블로그임"
            );

            // when & then
            assertThatThrownBy(() ->
                    referenceLinkService.save(command)
            ).isInstanceOf(NoAuthorityBlogException.class);
        }
    }
}
