package com.mallang.comment.domain.writer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("인증된 댓글 작성자(AuthenticatedWriter) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthenticatedWriterTest {

    @Test
    void 비밀_댓글을_작성할_수_있다() {
        // given
        AuthenticatedWriter mallang = new AuthenticatedWriter(1L);

        // when & then
        assertThat(mallang.canWriteSecret()).isTrue();
    }

    @Nested
    class 권한_확인_시 {

        @Test
        void 회원_ID가_일치하면_권한이_있다() {
            // given
            AuthenticatedWriter mallang = new AuthenticatedWriter(1L);

            // when
            boolean result = mallang.hasAuthority(new AuthenticatedWriterCredential(1L));

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 회원_ID가_일치하지_않으면_권한이_없다() {
            // given
            AuthenticatedWriter mallang = new AuthenticatedWriter(1L);

            // when
            boolean result = mallang.hasAuthority(new AuthenticatedWriterCredential(2L));

            // then
            assertThat(result).isFalse();
        }

        @Test
        void Credential_타입이_인증된_작성자를_위한_타입이_아니면_권한이_없다() {
            // given
            AuthenticatedWriter mallang = new AuthenticatedWriter(1L);

            // when
            boolean result = mallang.hasAuthority(new AnonymousWriterCredential("123"));

            // then
            assertThat(result).isFalse();
        }
    }
}
