package com.mallang.post.application.command;

import com.mallang.blog.domain.BlogName;
import java.util.List;

public record PostDeleteCommand(
        Long memberId,
        BlogName blogName,
        List<Long> postIds
) {
}
