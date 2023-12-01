package com.mallang.statistics.batch;

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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Configuration
public class BlogVisitStatisticJobConfig {

    private final BlogVisitHistoryRepository blogVisitHistoryRepository;
    private final BlogVisitStatisticRepository blogVisitStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    @Bean
    public Job blogVisitStatisticJob(
            JobRepository jobRepository,
            Step blogVisitsHistoryAccumulateStep
    ) {
        return new JobBuilder("blogVisitStatisticJob", jobRepository)
                .start(blogVisitsHistoryAccumulateStep)
                .build();
    }

    @JobScope
    @Bean
    public Step blogVisitsHistoryAccumulateStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("blogVisitsHistoryAccumulateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    blogVisitsAccumulateTask();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    public void blogVisitsAccumulateTask() {
        Map<String, List<BlogVisitHistory>> unprocessedHistories = getUnAccumulatedVisits();
        Map<String, Map<LocalDate, List<BlogVisitHistory>>> historiesGroupedByDateByPostId =
                groupingViewByDate(unprocessedHistories);
        accumulateVisits(historiesGroupedByDateByPostId);
    }

    private Map<String, List<BlogVisitHistory>> getUnAccumulatedVisits() {
        return blogVisitHistoryRepository.findAll()
                .stream()
                .collect(groupingBy(BlogVisitHistory::getBlogName));
    }

    private Map<String, Map<LocalDate, List<BlogVisitHistory>>> groupingViewByDate(
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

    private void accumulateVisits(Map<String, Map<LocalDate, List<BlogVisitHistory>>> historiesGroupByDateByPostId) {
        for (Entry<String, Map<LocalDate, List<BlogVisitHistory>>> entry : historiesGroupByDateByPostId.entrySet()) {
            String blogName = entry.getKey();
            Map<LocalDate, List<BlogVisitHistory>> viewHistoriesGroupByDate = entry.getValue();
            aggregateVisitsByPostId(blogName, viewHistoriesGroupByDate);
        }
    }

    private void aggregateVisitsByPostId(String blogName,
                                         Map<LocalDate, List<BlogVisitHistory>> viewHistoriesGroupByDate) {
        for (Entry<LocalDate, List<BlogVisitHistory>> entry : viewHistoriesGroupByDate.entrySet()) {
            aggregateEachPostVisitsByDate(blogName, entry.getKey(), entry.getValue());
        }
    }

    private void aggregateEachPostVisitsByDate(String blogName,
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
