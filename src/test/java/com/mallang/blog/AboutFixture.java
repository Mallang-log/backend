package com.mallang.blog;

import com.mallang.blog.domain.About;
import com.mallang.blog.domain.Blog;
import org.springframework.test.util.ReflectionTestUtils;

public class AboutFixture {

    public static About about(Long id, Blog blog) {
        About about = about(blog);
        ReflectionTestUtils.setField(about, "id", id);
        return about;
    }

    public static About about(Blog blog) {
        return new About(
                blog,
                "content",
                blog.getOwner()
        );
    }
}
