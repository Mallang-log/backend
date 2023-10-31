package com.mallang.category.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.command.CreateCategoryCommand;
import jakarta.annotation.Nullable;

public record CreateCategoryRequest(
        String name,
        @Nullable Long parentCategoryId
) {
    public CreateCategoryCommand toCommand(Long memberId, BlogName blogName) {
        return CreateCategoryCommand.builder()
                .name(name)
                .memberId(memberId)
                .blogName(blogName)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
