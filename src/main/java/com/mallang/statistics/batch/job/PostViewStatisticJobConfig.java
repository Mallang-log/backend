package com.mallang.statistics.batch.job;

import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.PostViewStatisticRepository;
import com.mallang.statistics.statistic.source.PostViewHistoryRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class PostViewStatisticJobConfig {

    private static final int CHUNK_SIZE = 500;

    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;
    private final PostViewStatisticRepository postViewStatisticRepository;
    private final PostViewHistoryRepository postViewHistoryRepository;
    private final StartAndEndTimeValidator startAndEndTimeValidator;

    @Bean
    public Job postViewStatisticJob() {
        return new JobBuilder("postViewStatisticJob", jobRepository)
                .start(postViewStatisticStep())
                .next(postViewHistoryDeleteStep(null, null))
                .validator(startAndEndTimeValidator)
                .build();
    }

    @Bean
    public Step postViewStatisticStep() {
        return new StepBuilder("postViewStatisticStep", jobRepository)
                .<PostViewHistoryDto, PostViewStatistic>chunk(CHUNK_SIZE, txManager)
                .reader(postViewHistoryDtoReader(null, null))
                .processor(postViewHistoryToStatisticProcessor())
                .writer(postViewStatisticWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step postViewHistoryDeleteStep(
            @Value("#{jobParameters[startInclude]}") LocalDateTime startInclude,
            @Value("#{jobParameters[endExclude]}") LocalDateTime endExclude
    ) {
        return new StepBuilder("postViewHistoryDeleteStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    postViewHistoryRepository.deleteWithCreatedDateBetweenIncludeStartAndExcludeEnd(
                            startInclude, endExclude
                    );
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<PostViewHistoryDto> postViewHistoryDtoReader(
            @Value("#{jobParameters[startInclude]}") LocalDateTime startInclude,
            @Value("#{jobParameters[endExclude]}") LocalDateTime endExclude
    ) {
        return new JdbcCursorItemReaderBuilder<PostViewHistoryDto>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new DataClassRowMapper<>(PostViewHistoryDto.class))
                .sql("""
                           SELECT pvh.post_id, pvh.blog_id, pvh.date, COUNT(*) as view_count
                           FROM post_view_history pvh
                           WHERE pvh.created_date >= ? AND pvh.created_date < ?
                           GROUP BY pvh.post_id, pvh.blog_id, pvh.date
                        """)
                .name("postViewHistoryDtoReader")
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{startInclude, endExclude}))
                .build();
    }

    @Bean
    public ItemProcessor<PostViewHistoryDto, PostViewStatistic> postViewHistoryToStatisticProcessor() {
        return historyDto -> {
            PostId postId = new PostId(historyDto.postId(), historyDto.blogId());
            PostViewStatistic postViewStatistic = postViewStatisticRepository
                    .findByPostIdAndStatisticDate(postId, historyDto.date())
                    .orElseGet(() -> postViewStatisticRepository.save(
                            new PostViewStatistic(historyDto.date(), postId)
                    ));
            postViewStatistic.addCount(historyDto.viewCount());
            return postViewStatistic;
        };
    }

    @Bean
    public ItemWriter<PostViewStatistic> postViewStatisticWriter() {
        return new JpaItemWriterBuilder<PostViewStatistic>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
