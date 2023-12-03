package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.reference.exception.BadReferenceLinkTitleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("참고 링크의 제목 (ReferenceLinkTitle) 은(는)")
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
                "\n",
        })
        void 제목은_공백으로만_이루어져_있으면_안된다(String title) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLinkTitle(title)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 앞뒤_공백은_제거된다() {
            // when
            ReferenceLinkTitle referenceLinkTitle = new ReferenceLinkTitle(" \n 1 \n ");

            // then
            assertThat(referenceLinkTitle.getTitle()).isEqualTo("1");
        }

        @Test
        void 앞뒤_공백을_제거하고_제목이_100글자보다_길면_잘라내진다() {
            // given
            String size100 = "1".repeat(100);
            String size101 = "1".repeat(101);

            // when
            ReferenceLinkTitle titleSize100 = new ReferenceLinkTitle(size100);
            ReferenceLinkTitle titleSize101 = new ReferenceLinkTitle(size101);

            // then
            assertThat(titleSize100.getTitle()).isEqualTo(size100);
            assertThat(titleSize101.getTitle()).isEqualTo("1".repeat(96) + " ...");
        }
    }
}
