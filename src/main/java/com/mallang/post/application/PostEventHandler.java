package com.mallang.post.application;

import com.mallang.category.domain.event.CategoryDeletedEvent;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostEventHandler {

    private final PostRepository postRepository;

    @EventListener(CategoryDeletedEvent.class)
    void deleteCategoryFromPost(CategoryDeletedEvent event) {
        List<Post> posts = postRepository.findAllByCategoryId(event.categoryId());
        for (Post post : posts) {
            post.setCategory(null);
        }
    }
}
