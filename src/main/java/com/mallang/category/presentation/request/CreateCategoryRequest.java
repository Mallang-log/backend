package com.mallang.category.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.command.CreateCategoryCommand;
import jakarta.annotation.Nullable;

public record CreateCategoryRequest(
        BlogName blogName,
        String name,
        @Nullable Long parentCategoryId
) {

    public CreateCategoryRequest(String blogName, String name, @Nullable Long parentCategoryId) {
        this(new BlogName(blogName), name, parentCategoryId);
    }

    public CreateCategoryCommand toCommand(Long memberId) {
        return CreateCategoryCommand.builder()
                .name(name)
                .memberId(memberId)
                .blogName(blogName)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
