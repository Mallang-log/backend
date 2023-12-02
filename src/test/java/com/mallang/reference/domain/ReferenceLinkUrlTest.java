package com.mallang.reference.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mallang.reference.exception.BadReferenceLinkUrlException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("첨조 링크의 URL (ReferenceLinkUrl) 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ReferenceLinkUrlTest {

    @Test
    void url에_값이_존재해야_하며_공백으로만_이루어져서는_안된다() {
        // when & then
        assertThatThrownBy(() ->
                new ReferenceLinkUrl(null)
        ).isInstanceOf(BadReferenceLinkUrlException.class);
        assertThatThrownBy(() ->
                new ReferenceLinkUrl("")
        ).isInstanceOf(BadReferenceLinkUrlException.class);
        assertThatThrownBy(() ->
                new ReferenceLinkUrl("  ")
        ).isInstanceOf(BadReferenceLinkUrlException.class);
    }

    @Test
    void url의_앞_뒤_공백은_제거된다() {
        // when
        ReferenceLinkUrl referenceLink = new ReferenceLinkUrl("   d d   ");

        // then
        assertThat(referenceLink.getUrl()).isEqualTo("d d");
    }
}
