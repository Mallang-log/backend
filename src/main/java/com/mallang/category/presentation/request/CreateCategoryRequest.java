package com.mallang.category.presentation.request;

import com.mallang.category.application.command.CreateCategoryCommand;
import jakarta.annotation.Nullable;

public record CreateCategoryRequest(
        String blogName,
        String name,
        @Nullable Long parentCategoryId
) {

    public CreateCategoryCommand toCommand(Long memberId) {
        return CreateCategoryCommand.builder()
                .name(name)
                .blogName(blogName)
                .memberId(memberId)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
