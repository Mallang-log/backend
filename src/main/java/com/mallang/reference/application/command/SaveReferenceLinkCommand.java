package com.mallang.reference.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.reference.domain.ReferenceLink;
import jakarta.annotation.Nullable;

public record SaveReferenceLinkCommand(
        Long memberId,
        String url,
        String title,
        @Nullable String memo
) {
    public ReferenceLink toReferenceLink(Member member) {
        return new ReferenceLink(url, title, memo, member);
    }
}
