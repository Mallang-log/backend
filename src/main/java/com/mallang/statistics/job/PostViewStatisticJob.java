package com.mallang.statistics.job;

import static java.util.stream.Collectors.groupingBy;

import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import com.mallang.statistics.statistic.source.PostViewHistory;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Component
public class PostViewStatisticJob {

    private final PostViewHistoryRepository postViewHistoryRepository;
    private final PostViewStatisticRepository postViewStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    public void postViewsAggregationJob() {
        Map<Long, List<PostViewHistory>> unprocessedHistories = getUnAggregatedViewsStep();
        Map<Long, Map<LocalDate, List<PostViewHistory>>> historiesGroupedByDateByPostId =
                groupingViewByDateStep(unprocessedHistories);
        aggregateViewsStep(historiesGroupedByDateByPostId);
    }

    private Map<Long, List<PostViewHistory>> getUnAggregatedViewsStep() {
        return postViewHistoryRepository.findAll()
                .stream()
                .collect(groupingBy(PostViewHistory::getPostId));
    }

    private Map<Long, Map<LocalDate, List<PostViewHistory>>> groupingViewByDateStep(
            Map<Long, List<PostViewHistory>> unAggregatedViews
    ) {
        return unAggregatedViews
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .collect(groupingBy(it -> it.getCreatedDate().toLocalDate()))
                ));
    }

    private void aggregateViewsStep(Map<Long, Map<LocalDate, List<PostViewHistory>>> historiesGroupByDateByPostId) {
        for (Entry<Long, Map<LocalDate, List<PostViewHistory>>> entry : historiesGroupByDateByPostId.entrySet()) {
            Long postId = entry.getKey();
            Map<LocalDate, List<PostViewHistory>> viewHistoriesGroupByDate = entry.getValue();
            aggregateViewsByPostIdStep(postId, viewHistoriesGroupByDate);
        }
    }

    private void aggregateViewsByPostIdStep(Long postId,
                                            Map<LocalDate, List<PostViewHistory>> viewHistoriesGroupByDate) {
        for (Entry<LocalDate, List<PostViewHistory>> entry : viewHistoriesGroupByDate.entrySet()) {
            aggregateEachPostViewsByDateStep(postId, entry.getKey(), entry.getValue());
        }
    }

    private void aggregateEachPostViewsByDateStep(Long postId,
                                                  LocalDate date,
                                                  List<PostViewHistory> postViewHistories) {
        transactionTemplate.executeWithoutResult(status -> {
            PostViewStatistic postViewStatistic = postViewStatisticRepository
                    .findByPostIdAndStatisticDate(postId, date)
                    .orElseGet(() -> postViewStatisticRepository.save(new PostViewStatistic(date, postId)));
            postViewStatistic.addCount(postViewHistories.size());
            postViewHistoryRepository.deleteAll(postViewHistories);
        });
    }
}
