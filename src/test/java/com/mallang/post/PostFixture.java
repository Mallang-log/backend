package com.mallang.post;

import com.mallang.blog.domain.Blog;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.domain.PostVisibilityPolicy.Visibility;
import java.util.Collections;

public class PostFixture {

    public static Post publicPost(Long id, Blog blog) {
        return new Post(
                new PostId(id, blog.getId()),
                blog,
                Visibility.PUBLIC,
                null,
                "title",
                "intro",
                "content",
                "image",
                null,
                Collections.emptyList(),
                blog.getOwner()
        );
    }

    public static Post protectedPost(Long id, Blog blog, String password) {
        return new Post(
                new PostId(id, blog.getId()),
                blog,
                Visibility.PROTECTED,
                password,
                "title",
                "intro",
                "content",
                "image",
                null,
                Collections.emptyList(),
                blog.getOwner()
        );
    }

    public static Post privatePost(Long id, Blog blog) {
        return new Post(
                new PostId(id, blog.getId()),
                blog,
                Visibility.PRIVATE,
                null,
                "title",
                "intro",
                "content",
                "image",
                null,
                Collections.emptyList(),
                blog.getOwner()
        );
    }
}
