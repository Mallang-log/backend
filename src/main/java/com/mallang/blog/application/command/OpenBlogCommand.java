package com.mallang.blog.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;

public record OpenBlogCommand(
        Long memberId,
        String name
) {
    public Blog toBlog(Member member) {
        return Blog.builder()
                .name(name)
                .owner(member)
                .build();
    }
}
