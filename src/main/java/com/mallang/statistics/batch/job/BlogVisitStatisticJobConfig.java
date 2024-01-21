package com.mallang.statistics.batch.job;

import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.BlogVisitStatisticRepository;
import com.mallang.statistics.statistic.source.BlogVisitHistoryRepository;
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
public class BlogVisitStatisticJobConfig {

    private static final int CHUNK_SIZE = 500;

    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager txManager;
    private final BlogVisitStatisticRepository blogVisitStatisticRepository;
    private final BlogVisitHistoryRepository blogVisitHistoryRepository;
    private final StartAndEndTimeValidator startAndEndTimeValidator;

    @Bean
    public Job blogVisitStatisticJob() {
        return new JobBuilder("blogVisitStatisticJob", jobRepository)
                .start(blogVisitStatisticStep())
                .next(blogVisitHistoryDeleteStep(null, null))
                .validator(startAndEndTimeValidator)
                .build();
    }

    @Bean
    public Step blogVisitStatisticStep() {
        return new StepBuilder("blogVisitStatisticStep", jobRepository)
                .<BlogVisitHistoryDto, BlogVisitStatistic>chunk(CHUNK_SIZE, txManager)
                .reader(blogVisitHistoryDtoReader(null, null))
                .processor(blogVisitHistoryToStatisticProcessor())
                .writer(blogVisitStatisticWriter())
                .build();
    }

    @Bean
    @JobScope
    public Step blogVisitHistoryDeleteStep(
            @Value("#{jobParameters[startInclude]}") LocalDateTime startInclude,
            @Value("#{jobParameters[endExclude]}") LocalDateTime endExclude
    ) {
        return new StepBuilder("blogVisitHistoryDeleteStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    blogVisitHistoryRepository.deleteWithCreatedDateBetweenIncludeStartAndExcludeEnd(
                            startInclude, endExclude
                    );
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<BlogVisitHistoryDto> blogVisitHistoryDtoReader(
            @Value("#{jobParameters[startInclude]}") LocalDateTime startInclude,
            @Value("#{jobParameters[endExclude]}") LocalDateTime endExclude
    ) {
        return new JdbcCursorItemReaderBuilder<BlogVisitHistoryDto>()
                .fetchSize(CHUNK_SIZE)
                .dataSource(dataSource)
                .rowMapper(new DataClassRowMapper<>(BlogVisitHistoryDto.class))
                .sql("""
                           SELECT bvh.blog_name, bvh.date, COUNT(*) as visit_count
                           FROM blog_visit_history bvh
                           WHERE bvh.created_date >= ? AND bvh.created_date < ?
                           GROUP BY bvh.blog_name, bvh.date
                        """)
                .name("blogVisitHistoryDtoReader")
                .preparedStatementSetter(new ArgumentPreparedStatementSetter(new Object[]{startInclude, endExclude}))
                .build();
    }

    @Bean
    public ItemProcessor<BlogVisitHistoryDto, BlogVisitStatistic> blogVisitHistoryToStatisticProcessor() {
        return historyDto -> {
            BlogVisitStatistic blogVisitStatistic = blogVisitStatisticRepository
                    .findByBlogNameAndStatisticDate(historyDto.blogName(), historyDto.date())
                    .orElseGet(() -> blogVisitStatisticRepository.save(
                            new BlogVisitStatistic(historyDto.date(), historyDto.blogName())
                    ));
            blogVisitStatistic.addCount(historyDto.visitCount());
            return blogVisitStatistic;
        };
    }

    @Bean
    public ItemWriter<BlogVisitStatistic> blogVisitStatisticWriter() {
        return new JpaItemWriterBuilder<BlogVisitStatistic>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
