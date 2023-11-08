package com.mallang.post.application;

import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostEventHandler {

    private final PostRepository postRepository;

    @EventListener(CategoryDeletedEvent.class)
    void deletePostCategory(CategoryDeletedEvent event) {
        List<Post> posts = postRepository.findAllByBlogNameAndCategoryId(event.blogName(), event.categoryId());
        for (Post post : posts) {
            post.removeCategory();
        }
    }
}
