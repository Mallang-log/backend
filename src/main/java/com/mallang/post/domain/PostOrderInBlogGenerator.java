package com.mallang.post.domain;

import com.mallang.blog.domain.Blog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostOrderInBlogGenerator {

    private final PostRepository postRepository;

    public Long generate(Blog blog) {
        return postRepository.countByBlog(blog) + 1;
    }
}
