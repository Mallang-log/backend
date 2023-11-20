package com.mallang.post.query.data;

import com.mallang.post.domain.visibility.PostVisibilityPolicy.Visibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PostManageSearchCond(
        @NotNull String blogName,
        @Nullable String title,
        @Nullable String content,
        @Nullable Long categoryId,
        @Nullable Visibility visibility
) {
    public static final long NO_CATEGORY_CONDITION = -1L;
}
