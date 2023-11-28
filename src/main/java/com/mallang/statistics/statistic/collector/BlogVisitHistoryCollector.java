package com.mallang.statistics.statistic.collector;

import com.mallang.statistics.statistic.source.BlogVisitHistory;
import com.mallang.statistics.statistic.source.BlogVisitHistoryRepository;
import com.mallang.statistics.statistic.source.CommonHistory;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class BlogVisitHistoryCollector {

    private static final int BLOG_VISIT_REFRESH_DAY = 1;

    private final BlogVisitHistoryRepository blogVisitHistoryRepository;

    public void save(BlogVisitHistory blogVisitHistory) {
        UUID uuid = blogVisitHistory.getUuid();
        String blogName = blogVisitHistory.getBlogName();
        blogVisitHistoryRepository
                .findFirstByUuidAndBlogNameOrderByCreatedDateDesc(uuid, blogName)
                .map(CommonHistory::getCreatedDate)
                .map(lastTime -> Duration.between(lastTime, blogVisitHistory.getCreatedDate()).toDays())
                .ifPresentOrElse(
                        dayDifference -> {
                            if (dayDifference >= BLOG_VISIT_REFRESH_DAY) {
                                blogVisitHistoryRepository.save(blogVisitHistory);
                            }
                        },
                        () -> {
                            blogVisitHistoryRepository.save(blogVisitHistory);
                        }
                );
    }
}
