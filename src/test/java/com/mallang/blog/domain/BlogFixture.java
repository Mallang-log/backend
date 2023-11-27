package com.mallang.blog.domain;

import com.mallang.auth.domain.Member;
import org.springframework.test.util.ReflectionTestUtils;

public class BlogFixture {

    public static final String MALLANG_BLOG_NAME = "mallang-log";

    public static Blog mallangBlog(Member owner) {
        return mallangBlog(null, owner);
    }

    public static Blog mallangBlog(Long id, Member owner) {
        Blog blog = Blog.builder()
                .name(MALLANG_BLOG_NAME)
                .owner(owner)
                .build();
        ReflectionTestUtils.setField(blog, "id", id);
        return blog;
    }
}
