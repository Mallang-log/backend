package com.mallang.statistics.statistic.collector;

import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.source.CommonHistory;
import com.mallang.statistics.statistic.source.PostViewHistory;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
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
        PostId postId = postViewHistory.getPostId();
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
