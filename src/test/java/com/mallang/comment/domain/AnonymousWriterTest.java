package com.mallang.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mallang.comment.domain.credential.AnonymousWriterCredential;
import com.mallang.comment.domain.credential.AuthenticatedWriterWriterCredential;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("익명 댓글 작성자(AnonymousWriter) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AnonymousWriterTest {

    @Nested
    class 권한_확인_시 {

        @Test
        void 암호가_일치하면_권한이_있다() {
            // given
            AnonymousWriter mallang = new AnonymousWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new AnonymousWriterCredential("123"));

            // then
            assertThat(result).isTrue();
        }

        @Test
        void 암호가_일치하지_않으면_권한이_없다() {
            // given
            AnonymousWriter mallang = new AnonymousWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new AnonymousWriterCredential("12"));

            // then
            assertThat(result).isFalse();
        }

        @Test
        void Credential_타입이_익명_작성자를_위한_타입이_아니면_권한이_없다() {
            // given
            AnonymousWriter mallang = new AnonymousWriter("mallang", "123");

            // when
            boolean result = mallang.hasAuthority(new AuthenticatedWriterWriterCredential(1L));

            // then
            assertThat(result).isFalse();
        }
    }
}
