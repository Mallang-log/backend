package com.mallang.blog.application.command;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.About;
import com.mallang.blog.domain.Blog;

public record WriteAboutCommand(
        Long memberId,
        String blogName,
        String content
) {
    public About toAbout(Member member, Blog blog) {
        return new About(blog, content, member);
    }
}
