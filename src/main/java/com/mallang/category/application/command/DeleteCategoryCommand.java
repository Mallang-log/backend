package com.mallang.category.application.command;

import com.mallang.blog.domain.BlogName;

public record DeleteCategoryCommand(
        Long memberId,
        BlogName blogName,
        Long categoryId
) {
}
