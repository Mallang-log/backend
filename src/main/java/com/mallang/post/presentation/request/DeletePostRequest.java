package com.mallang.post.presentation.request;

import com.mallang.post.application.command.DeletePostCommand;
import java.util.List;

public record DeletePostRequest(
        List<Long> postIds,
        String blogName
) {
    public DeletePostCommand toCommand(Long memberId) {
        return new DeletePostCommand(memberId, postIds, blogName);
    }
}
