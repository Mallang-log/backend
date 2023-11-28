package com.mallang.statistics.job;

import static java.util.stream.Collectors.groupingBy;

import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import com.mallang.statistics.statistic.source.BlogVisitHistory;
import com.mallang.statistics.statistic.source.BlogVisitHistoryRepository;
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
public class BlogVisitStatisticJob {

    private final BlogVisitHistoryRepository blogVisitHistoryRepository;
    private final BlogVisitStatisticRepository blogVisitStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    public void blogVisitsAggregationJob() {
        Map<String, List<BlogVisitHistory>> unprocessedHistories = getUnAggregatedVisitsStep();
        Map<String, Map<LocalDate, List<BlogVisitHistory>>> historiesGroupedByDateByPostId =
                groupingViewByDateStep(unprocessedHistories);
        aggregateVisitsStep(historiesGroupedByDateByPostId);
    }

    private Map<String, List<BlogVisitHistory>> getUnAggregatedVisitsStep() {
        return blogVisitHistoryRepository.findAll()
                .stream()
                .collect(groupingBy(BlogVisitHistory::getBlogName));
    }

    private Map<String, Map<LocalDate, List<BlogVisitHistory>>> groupingViewByDateStep(
            Map<String, List<BlogVisitHistory>> unAggregatedVisits
    ) {
        return unAggregatedVisits
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Entry::getKey,
                        entry -> entry.getValue()
                                .stream()
                                .collect(groupingBy(it -> it.getCreatedDate().toLocalDate()))
                ));
    }

    private void aggregateVisitsStep(Map<String, Map<LocalDate, List<BlogVisitHistory>>> historiesGroupByDateByPostId) {
        for (Entry<String, Map<LocalDate, List<BlogVisitHistory>>> entry : historiesGroupByDateByPostId.entrySet()) {
            String blogName = entry.getKey();
            Map<LocalDate, List<BlogVisitHistory>> viewHistoriesGroupByDate = entry.getValue();
            aggregateVisitsByPostIdStep(blogName, viewHistoriesGroupByDate);
        }
    }

    private void aggregateVisitsByPostIdStep(String blogName,
                                             Map<LocalDate, List<BlogVisitHistory>> viewHistoriesGroupByDate) {
        for (Entry<LocalDate, List<BlogVisitHistory>> entry : viewHistoriesGroupByDate.entrySet()) {
            aggregateEachPostVisitsByDateStep(blogName, entry.getKey(), entry.getValue());
        }
    }

    private void aggregateEachPostVisitsByDateStep(String blogName,
                                                   LocalDate date,
                                                   List<BlogVisitHistory> blogVisitStatistics) {
        transactionTemplate.executeWithoutResult(status -> {
            BlogVisitStatistic blogVisitStatistic = blogVisitStatisticRepository
                    .findByBlogNameAndStatisticDate(blogName, date)
                    .orElseGet(() -> blogVisitStatisticRepository.save(new BlogVisitStatistic(date, blogName)));
            blogVisitStatistic.addCount(blogVisitStatistics.size());
            blogVisitHistoryRepository.deleteAll(blogVisitStatistics);
        });
    }
}
