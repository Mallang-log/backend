package com.mallang.statistics.statistic.application;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostViewHistoryEventHandler {

    private final PostViewHistoryRepository postViewHistoryRepository;

    @EventListener(PostDeleteEvent.class)
    void deletePostViewHistory(PostDeleteEvent event) {
        postViewHistoryRepository.deleteAllByPostId(event.postId());
    }
}
