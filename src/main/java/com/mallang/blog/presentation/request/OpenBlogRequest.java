package com.mallang.blog.presentation.request;

import com.mallang.blog.application.command.OpenBlogCommand;

public record OpenBlogRequest(
        String name
) {
    public OpenBlogCommand toCommand(Long memberId) {
        return new OpenBlogCommand(memberId, name);
    }
}
