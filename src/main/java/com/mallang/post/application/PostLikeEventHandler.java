package com.mallang.post.application;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.post.domain.like.PostLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostLikeEventHandler {

    private final PostLikeRepository postLikeRepository;

    @EventListener(PostDeleteEvent.class)
    void deletePostLike(PostDeleteEvent event) {
        postLikeRepository.deleteAllByPostId(event.postId());
    }
}
