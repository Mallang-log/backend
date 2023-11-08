package com.mallang.post.application.command;

import com.mallang.blog.domain.BlogName;
import java.util.List;

public record DeletePostCommand(
        Long memberId,
        BlogName blogName,
        List<Long> postIds
) {
}
