package com.mallang.comment.domain.writer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("익명 댓글 작성자(UnAuthenticatedWriter) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UnAuthenticatedWriterTest {

    @Test
    void 비밀_댓글을_쓸_수_없다() {
        // given
        UnAuthenticatedWriter mallang = new UnAuthenticatedWriter("mallang", "123");

        // when & then
        assertThat(mallang.canWriteSecret()).isFalse();
    }

    @Nested
    class 권한_확인_시 {

        @Test
        void 암호가_일치하면_권한이_있다() {
            // given
            UnAuthenticatedWriter mallang = new UnAuthenticatedWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new UnAuthenticatedWriterCredential("123"));

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 암호가_일치하지_않으면_권한이_없다() {
            // given
            UnAuthenticatedWriter mallang = new UnAuthenticatedWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new UnAuthenticatedWriterCredential("12"));

            // then
            assertThat(result).isFalse();
        }

        @Test
        void Credential_타입이_익명_작성자를_위한_타입이_아니면_권한이_없다() {
            // given
            UnAuthenticatedWriter mallang = new UnAuthenticatedWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new AuthenticatedWriterCredential(1L));

            // then
            assertThat(result).isFalse();
        }
    }
}
