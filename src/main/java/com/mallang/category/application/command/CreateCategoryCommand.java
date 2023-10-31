package com.mallang.category.application.command;

import com.mallang.blog.domain.BlogName;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record CreateCategoryCommand(
        Long memberId,
        BlogName blogName,
        String name,
        @Nullable Long parentCategoryId
) {
}
