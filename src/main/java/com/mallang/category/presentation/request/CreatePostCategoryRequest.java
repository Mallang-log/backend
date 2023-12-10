package com.mallang.category.presentation.request;

import com.mallang.category.application.command.CreatePostCategoryCommand;
import jakarta.annotation.Nullable;

public record CreatePostCategoryRequest(
        String blogName,
        String name,
        @Nullable Long parentCategoryId,
        @Nullable Long prevCategoryId,
        @Nullable Long nextCategoryId

) {
    public CreatePostCategoryCommand toCommand(Long memberId) {
        return CreatePostCategoryCommand.builder()
                .name(name)
                .blogName(blogName)
                .memberId(memberId)
                .parentId(parentCategoryId)
                .nextId(nextCategoryId)
                .prevId(prevCategoryId)
                .build();
    }
}
