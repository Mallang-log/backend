package com.mallang.post.query.data;

import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record PostSearchCond(
        @Nullable Long categoryId,
        @Nullable String tag,
        @Nullable Long writerId,
        @Nullable String title,
        @Nullable String content,
        @Nullable String titleOrContent
) {
}
