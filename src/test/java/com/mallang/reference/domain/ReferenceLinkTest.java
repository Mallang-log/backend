package com.mallang.reference.domain;

import static com.mallang.auth.OauthMemberFixture.깃허브_동훈;
import static com.mallang.auth.OauthMemberFixture.깃허브_말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.mallang.auth.domain.Member;
import com.mallang.auth.domain.OauthMember;
import com.mallang.reference.exception.BadReferenceLinkMemoException;
import com.mallang.reference.exception.BadReferenceLinkTitleException;
import com.mallang.reference.exception.BadReferenceLinkUrlException;
import com.mallang.reference.exception.NoAuthorityLabelException;
import com.mallang.reference.exception.NoAuthorityReferenceLinkException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("참고 링크 (ReferenceLink) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkTest {

    private final Member member = 깃허브_말랑(1L);
    private final Member other = 깃허브_동훈(2L);
    private final Label memberLabel = new Label("Spring", member, "#000000");
    private final Label otherLabel = new Label("Spring", other, "#000000");

    @Nested
    class 생성_시 {

        @ParameterizedTest
        @NullAndEmptySource
        void 제목_없이_생성이_불가능하다(String nullAndEmptyTitle) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLink("url", nullAndEmptyTitle, "memo", member, null)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "",
                " ",
                " \n ",
        })
        void 제목은_공백으로만_이루어져_있으면_안된다(String title) {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLink("url", title, "memo", member, null)
            ).isInstanceOf(BadReferenceLinkTitleException.class);
        }

        @Test
        void 제목의_앞뒤_공백은_제거된다() {
            // when
            ReferenceLink link = new ReferenceLink("url", " \n 1 \n ", null, member, null);

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
            ReferenceLink referenceLink = new ReferenceLink("url", "title", nullAndEmptyMemo, member, null);

            // then
            assertThat(referenceLink.getMemo()).isEqualTo(nullAndEmptyMemo);
        }

        @Test
        void 메모의_최대_길이는_300글자이다() {
            // when & then
            assertDoesNotThrow(() ->
                    new ReferenceLink("url", "title", "1".repeat(300), member, null)
            );
            assertThatThrownBy(() ->
                    new ReferenceLink("url", "title", "1".repeat(301), member, null)
            ).isInstanceOf(BadReferenceLinkMemoException.class);
        }

        @Test
        void url에_값이_존재해야_하며_공백으로만_이루어져서는_안된다() {
            // when & then
            assertThatThrownBy(() ->
                    new ReferenceLink(null, "title1", "", member, null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    new ReferenceLink("", "title1", "", member, null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    new ReferenceLink("  ", "title1", "", member, null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
        }

        @Test
        void url의_앞_뒤_공백은_제거된다() {
            // when
            ReferenceLink referenceLink = new ReferenceLink("   d d   ", "title1", "", member, null);

            // then
            assertThat(referenceLink.getUrl()).isEqualTo("d d");
        }

        @Test
        void 라벨을_설정할_수_있다() {
            // when
            ReferenceLink referenceLink = new ReferenceLink(
                    "https://ttl-tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그",
                    member,
                    memberLabel
            );

            // then
            assertThat(referenceLink.getLabel())
                    .isEqualTo(memberLabel);
        }

        @Test
        void 다른_사람의_라벨이라면_예외() {
            // when & then
            assertThatThrownBy(() -> {
                new ReferenceLink(
                        "https://ttl-tistory.com",
                        "말랑이 블로그",
                        "말랑이 블로그",
                        member,
                        otherLabel
                );
            }).isInstanceOf(NoAuthorityLabelException.class);
        }
    }

    @Test
    void 회원에_대한_권한_검증을_한다() {
        // given
        OauthMember otherMember = 깃허브_동훈(member.getId() + 1);
        ReferenceLink referenceLink = new ReferenceLink("url", "title1", null, member, null);

        // when & then
        assertDoesNotThrow(() -> {
            referenceLink.validateMember(member);
        });
        assertThatThrownBy(() -> {
            referenceLink.validateMember(otherMember);
        }).isInstanceOf(NoAuthorityReferenceLinkException.class);
    }

    @Nested
    class 수정_시 {

        private final ReferenceLink link = new ReferenceLink("url", "title", "memo", member, null);

        @ParameterizedTest
        @NullAndEmptySource
        void 제목을_없앨_수_없다(String nullAndEmptyTitle) {
            // when & then
            assertThatThrownBy(() ->
                    link.update("url", nullAndEmptyTitle, "memo", null)
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
                    link.update("url", title, "memo", null)
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
                link.update("url", "title", nullAndEmptyMemo, null);
            });
        }

        @Test
        void 메모의_최대_길이는_300글자이다() {
            // when & then
            assertDoesNotThrow(() -> {
                link.update("url", "title", "1".repeat(300), null);
            });
            assertThatThrownBy(() ->
                    link.update("url", "title", "1".repeat(301), null)
            ).isInstanceOf(BadReferenceLinkMemoException.class);
        }

        @Test
        void url에_값이_존재해야_하며_공백으로만_이루어져서는_안된다() {
            // when & then
            assertThatThrownBy(() ->
                    link.update(null, "title1", "", null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    link.update("", "title1", "", null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
            assertThatThrownBy(() ->
                    link.update("  ", "title1", "", null)
            ).isInstanceOf(BadReferenceLinkUrlException.class);
        }

        @Test
        void url의_앞_뒤_공백은_제거된다() {
            // when
            link.update("   d d   ", "title1", "", null);

            // then
            assertThat(link.getUrl()).isEqualTo("d d");
        }

        @Test
        void 라벨을_수정할_수_있다() {
            // given
            ReferenceLink referenceLink1 =
                    new ReferenceLink("url", "블로그", "블로그", member, memberLabel);
            ReferenceLink referenceLink2 =
                    new ReferenceLink("url", "블로그", "블로그", member, null);

            // when
            referenceLink1.update(
                    "https://ttl-tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그",
                    null
            );
            referenceLink2.update(
                    "https://ttl-tistory.com",
                    "말랑이 블로그",
                    "말랑이 블로그",
                    memberLabel
            );

            // then
            assertThat(referenceLink1.getLabel()).isNull();
            assertThat(referenceLink2.getLabel()).isEqualTo(memberLabel);
        }

        @Test
        void 다른_사람의_라벨이라면_예외() {
            // given
            ReferenceLink referenceLink =
                    new ReferenceLink("url", "블로그", "블로그", member, memberLabel);

            // when & then
            assertThatThrownBy(() -> {
                referenceLink.update(
                        "url",
                        "블로그",
                        "블로그",
                        otherLabel
                );
            }).isInstanceOf(NoAuthorityLabelException.class);
        }
    }
}
