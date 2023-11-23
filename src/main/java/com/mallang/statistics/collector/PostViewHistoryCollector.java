package com.mallang.statistics.collector;

import com.mallang.statistics.history.CommonHistory;
import com.mallang.statistics.history.PostViewHistory;
import com.mallang.statistics.history.PostViewHistoryRepository;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class PostViewHistoryCollector {

    private static final int POST_VIEW_REFRESH_HOUR = 1;

    private final PostViewHistoryRepository postViewHistoryRepository;

    public void save(PostViewHistory postViewHistory) {
        UUID uuid = postViewHistory.getUuid();
        Long postId = postViewHistory.getPostId();
        postViewHistoryRepository
                .findFirstByUuidAndPostIdOrderByCreatedDateDesc(uuid, postId)
                .map(CommonHistory::getCreatedDate)
                .map(lastTime -> Duration.between(lastTime, postViewHistory.getCreatedDate()).toHours())
                .ifPresentOrElse(
                        hoursDifference -> {
                            if (hoursDifference >= POST_VIEW_REFRESH_HOUR) {
                                postViewHistoryRepository.save(postViewHistory);
                            }
                        },
                        () -> {
                            postViewHistoryRepository.save(postViewHistory);
                        }
                );
    }
}
