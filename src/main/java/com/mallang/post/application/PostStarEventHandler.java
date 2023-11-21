package com.mallang.post.application;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.star.PostStarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostStarEventHandler {

    private final PostStarRepository postStarRepository;

    @EventListener(PostDeleteEvent.class)
    void deletePostLike(PostDeleteEvent event) {
        postStarRepository.deleteAllByPostId(event.postId());
    }
}
