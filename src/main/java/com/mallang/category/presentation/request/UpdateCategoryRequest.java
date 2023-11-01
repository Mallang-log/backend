package com.mallang.category.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.command.UpdateCategoryCommand;
import jakarta.annotation.Nullable;

public record UpdateCategoryRequest(
        BlogName blogName,
        String name,
        @Nullable Long parentCategoryId
) {

    public UpdateCategoryRequest(String blogName, String name, @Nullable Long parentCategoryId) {
        this(new BlogName(blogName), name, parentCategoryId);
    }

    public UpdateCategoryCommand toCommand(Long categoryId, Long memberId) {
        return UpdateCategoryCommand.builder()
                .categoryId(categoryId)
                .name(name)
                .memberId(memberId)
                .blogName(blogName)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
