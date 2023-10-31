package com.mallang.category.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.command.UpdateCategoryCommand;
import jakarta.annotation.Nullable;

public record UpdateCategoryRequest(
        String name,
        @Nullable Long parentCategoryId
) {
    public UpdateCategoryCommand toCommand(Long categoryId, Long memberId, BlogName blogName) {
        return UpdateCategoryCommand.builder()
                .categoryId(categoryId)
                .name(name)
                .memberId(memberId)
                .blogName(blogName)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
