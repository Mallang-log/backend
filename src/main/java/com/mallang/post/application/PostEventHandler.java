package com.mallang.post.application;

import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostCategoryDeletedEvent;
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

    @EventListener(PostCategoryDeletedEvent.class)
    void deletePostCategory(PostCategoryDeletedEvent event) {
        List<Post> posts = postRepository.findAllByCategoryId(event.categoryId());
        for (Post post : posts) {
            post.removeCategory();
        }
    }
}
