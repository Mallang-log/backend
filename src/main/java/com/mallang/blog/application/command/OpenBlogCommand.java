package com.mallang.blog.application.command;

import com.mallang.blog.domain.Blog;
import com.mallang.member.domain.Member;

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
