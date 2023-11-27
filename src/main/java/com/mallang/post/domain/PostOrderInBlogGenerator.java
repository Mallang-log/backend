package com.mallang.post.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostOrderInBlogGenerator {

    private final PostRepository postRepository;

    public PostId generate(Long blogId) {
        long postId = postRepository.countByBlog(blogId) + 1;
        return new PostId(postId, blogId);
    }
}
