package com.mallang.comment.application;

import com.mallang.comment.domain.CommentRepository;
import com.mallang.post.domain.PostDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentEventHandler {

    private final CommentRepository commentRepository;

    @EventListener(PostDeleteEvent.class)
    void deleteCommentsFromPost(PostDeleteEvent event) {
        commentRepository.deleteAllByPostId(event.postId());
    }
}
