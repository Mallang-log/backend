package com.mallang.category.presentation.request;

import com.mallang.category.application.command.CreateCategoryCommand;
import jakarta.annotation.Nullable;

public record CreateCategoryRequest(
        String blogName,
        String name,
        @Nullable Long parentCategoryId,
        @Nullable Long prevCategoryId,
        @Nullable Long nextCategoryId

) {
    public CreateCategoryCommand toCommand(Long memberId) {
        return CreateCategoryCommand.builder()
                .name(name)
                .blogName(blogName)
                .memberId(memberId)
                .parentId(parentCategoryId)
                .nextId(nextCategoryId)
                .prevId(prevCategoryId)
                .build();
    }
}
