package com.mallang.reference.application.command;

import com.mallang.blog.domain.Blog;
import com.mallang.reference.domain.ReferenceLink;
import jakarta.annotation.Nullable;

public record SaveReferenceLinkCommand(
        Long memberId,
        String blogName,
        String url,
        String title,
        @Nullable String memo
) {
    public ReferenceLink toReferenceLink(Blog blog) {
        return new ReferenceLink(url, title, memo, blog);
    }
}
