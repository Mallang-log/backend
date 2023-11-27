package com.mallang.post.application.command;

import java.util.List;

public record DeletePostCommand(
        Long memberId,
        List<Long> postIds,
        String blogName
) {
}
