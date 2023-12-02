package com.mallang.reference.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static com.mallang.blog.domain.BlogFixture.mallangBlog;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.reference.exception.BadReferenceLinkMemoException;
import com.mallang.reference.exception.BadReferenceLinkTitleException;
import com.mallang.reference.exception.BadReferenceLinkUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("참조 링크 (ReferenceLink) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkTest {

    private final Member member = 깃허브_말랑(1L);
    private final Blog blog = mallangBlog(member);

    @Nested
    class 생성_시 {

        @ParameterizedTest
        @NullAndEmptySource
        void 제목_없이_생성이_불가능하다(String nullAndEmptyTitle) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLink("url", nullAndEmptyTitle, "memo", blog)
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
                    new ReferenceLink("url", title, "memo", blog)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 제목의_앞뒤_공백은_제거된다() {
            // when
            ReferenceLink link = new ReferenceLink("url", " \n 1 \n ", null, blog);

            // then
            assertThat(link.getTitle()).isEqualTo("1");
        }

        @Test
        void 제목의_앞뒤_공백을_제거하고_제목이_100글자보다_길면_잘라내진다() {
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

        @ParameterizedTest
        @NullAndEmptySource
        void 메모는_없어도_된다(String nullAndEmptyMemo) {
            // when
            ReferenceLink referenceLink = new ReferenceLink("url", "title", nullAndEmptyMemo, blog);

            // then
            assertThat(referenceLink.getMemo()).isEqualTo(nullAndEmptyMemo);
        }

        @Test
        void 메모의_최대_길이는_300글자이다() {
            // when & then
            assertDoesNotThrow(() ->
                    new ReferenceLink("url", "title", "1".repeat(300), blog)
            );
            assertThatThrownBy(() ->
                    new ReferenceLink("url", "title", "1".repeat(301), blog)
            ).isInstanceOf(BadReferenceLinkMemoException.class);
        }

        @Test
        void url에_값이_존재해야_하며_공백으로만_이루어져서는_안된다() {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLink(null, "title1", "", blog)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    new ReferenceLink("", "title1", "", blog)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    new ReferenceLink("  ", "title1", "", blog)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
        }

        @Test
        void url의_앞_뒤_공백은_제거된다() {
            // when
            ReferenceLink referenceLink = new ReferenceLink("   d d   ", "title1", "", blog);

            // then
            assertThat(referenceLink.getUrl()).isEqualTo("d d");
        }
    }

    @Nested
    class 수정_시 {

        private final ReferenceLink link = new ReferenceLink("url", "title", "memo", blog);

        @ParameterizedTest
        @NullAndEmptySource
        void 제목을_없앨_수_없다(String nullAndEmptyTitle) {
            // when & then
            assertThatThrownBy(() ->
                    link.update("url", nullAndEmptyTitle, "memo")
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                "  ",
        })
        void 제목이_공백으로만_이루어져_있으면_안된다(String title) {
            // when & then
            assertThatThrownBy(() ->
                    link.update("url", title, "memo")
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 제목의_앞뒤_공백을_제거하고_제목이_100글자보다_길면_잘라내진다() {
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

        @ParameterizedTest
        @NullAndEmptySource
        void 메모는_없어도_된다(String nullAndEmptyMemo) {
            // when & then
            assertDoesNotThrow(() -> {
                link.update("url", "title", nullAndEmptyMemo);
            });
        }

        @Test
        void 메모의_최대_길이는_300글자이다() {
            // when & then
            assertDoesNotThrow(() -> {
                link.update("url", "title", "1".repeat(300));
            });
            assertThatThrownBy(() ->
                    link.update("url", "title", "1".repeat(301))
            ).isInstanceOf(BadReferenceLinkMemoException.class);
        }

        @Test
        void url에_값이_존재해야_하며_공백으로만_이루어져서는_안된다() {
            // when & then
            assertThatThrownBy(() ->
                    link.update(null, "title1", "")
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    link.update("", "title1", "")
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    link.update("  ", "title1", "")
            ).isInstanceOf(BadReferenceLinkUrlException.class);
        }

        @Test
        void url의_앞_뒤_공백은_제거된다() {
            // when
            link.update("   d d   ", "title1", "");

            // then
            assertThat(link.getUrl()).isEqualTo("d d");
        }
    }
}
