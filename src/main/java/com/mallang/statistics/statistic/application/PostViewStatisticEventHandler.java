package com.mallang.statistics.statistic.application;

import com.mallang.post.domain.PostDeleteEvent;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class PostViewStatisticEventHandler {

    private final PostViewStatisticRepository postViewStatisticRepository;

    @EventListener(PostDeleteEvent.class)
    void deletePostViewStatistic(PostDeleteEvent event) {
        postViewStatisticRepository.deleteAllByPostId(event.postId());
    }
}
