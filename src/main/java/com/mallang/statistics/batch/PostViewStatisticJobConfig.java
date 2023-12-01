package com.mallang.statistics.batch;

import static java.util.stream.Collectors.groupingBy;

import com.mallang.post.domain.PostId;
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
public class PostViewStatisticJobConfig {

    private final PostViewHistoryRepository postViewHistoryRepository;
    private final PostViewStatisticRepository postViewStatisticRepository;
    private final TransactionTemplate transactionTemplate;

    @Bean
    public Job postViewStatisticJob(
            JobRepository jobRepository,
            Step postViewHistoryAccumulateStep
    ) {
        return new JobBuilder("postViewStatisticJob", jobRepository)
                .start(postViewHistoryAccumulateStep)
                .build();
    }

    @JobScope
    @Bean
    public Step postViewHistoryAccumulateStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager
    ) {
        return new StepBuilder("postViewHistoryAccumulateStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    postViewsAggregationTask();
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    public void postViewsAggregationTask() {
        Map<PostId, List<PostViewHistory>> unprocessedHistories = getUnAggregatedViews();
        Map<PostId, Map<LocalDate, List<PostViewHistory>>> historiesGroupedByDateByPostId =
                groupingViewByDate(unprocessedHistories);
        aggregateViews(historiesGroupedByDateByPostId);
    }

    private Map<PostId, List<PostViewHistory>> getUnAggregatedViews() {
        return postViewHistoryRepository.findAll()
                .stream()
                .collect(groupingBy(PostViewHistory::getPostId));
    }

    private Map<PostId, Map<LocalDate, List<PostViewHistory>>> groupingViewByDate(
            Map<PostId, List<PostViewHistory>> unAggregatedViews
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

    private void aggregateViews(Map<PostId, Map<LocalDate, List<PostViewHistory>>> historiesGroupByDateByPostId) {
        for (Entry<PostId, Map<LocalDate, List<PostViewHistory>>> entry : historiesGroupByDateByPostId.entrySet()) {
            PostId postId = entry.getKey();
            Map<LocalDate, List<PostViewHistory>> viewHistoriesGroupByDate = entry.getValue();
            aggregateViewsByPostId(postId, viewHistoriesGroupByDate);
        }
    }

    private void aggregateViewsByPostId(PostId postId,
                                        Map<LocalDate, List<PostViewHistory>> viewHistoriesGroupByDate) {
        for (Entry<LocalDate, List<PostViewHistory>> entry : viewHistoriesGroupByDate.entrySet()) {
            aggregateEachPostViewsByDate(postId, entry.getKey(), entry.getValue());
        }
    }

    private void aggregateEachPostViewsByDate(PostId postId,
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
