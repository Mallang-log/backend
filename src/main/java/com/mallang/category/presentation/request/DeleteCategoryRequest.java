package com.mallang.category.presentation.request;

import com.mallang.blog.domain.BlogName;
import com.mallang.category.application.command.DeleteCategoryCommand;

public record DeleteCategoryRequest(
        BlogName blogName
) {

    public DeleteCategoryRequest(String blogName) {
        this(new BlogName(blogName));
    }

    public DeleteCategoryCommand toCommand(Long categoryId, Long memberId) {
        return new DeleteCategoryCommand(memberId, blogName, categoryId);
    }
}
