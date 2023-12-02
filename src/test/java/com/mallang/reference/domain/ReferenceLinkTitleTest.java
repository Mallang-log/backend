package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.reference.exception.BadReferenceLinkTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("참조 링크의 제목 (ReferenceLinkTitle) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkTitleTest {

    @Nested
    class 생성_시 {

        @ParameterizedTest
        @NullAndEmptySource
        void 제목_없이_생성이_불가능하다(String nullAndEmptyTitle) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLinkTitle(nullAndEmptyTitle)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                "  ",
        })
        void 제목은_공백으로만_이루어져_있으면_안된다(String title) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLinkTitle(title)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 제목은_최대_30글자이다() {
            // when & then
            assertDoesNotThrow(() ->
                    new ReferenceLinkTitle("1".repeat(30))
            );
            assertThatThrownBy(() ->
                    new ReferenceLinkTitle("1".repeat(31))
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 앞뒤_공백은_제거된다() {
            // when
            ReferenceLinkTitle referenceLinkTitle = new ReferenceLinkTitle("  1  ");

            // then
            assertThat(referenceLinkTitle.getTitle()).isEqualTo("1");
        }
    }
}
