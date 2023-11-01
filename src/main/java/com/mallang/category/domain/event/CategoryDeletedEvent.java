package com.mallang.category.domain.event;

import com.mallang.blog.domain.BlogName;

public record CategoryDeletedEvent(
        BlogName blogName,
        Long categoryId
) {
}
