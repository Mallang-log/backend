package com.mallang.statistics.batch.job;

import static com.mallang.common.utils.LocalDateTimeUtils.onlyHours;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BlogVisitStatisticJobScheduler {

    public static final String EACH_HOUR_CRON = "0 0 * * * *";

    private final JobLauncher jobLauncher;
    private final Job blogVisitStatisticJob;

    @Scheduled(cron = EACH_HOUR_CRON)
    public void runTask() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        log.info("블로그 방문수 통계 작업 실행 [실행시간: {}]", now);
        LocalDateTime startInclude = onlyHours(now.minusHours(2));
        LocalDateTime endExclude = onlyHours(now);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("startInclude", startInclude)
                .addLocalDateTime("endExclude", endExclude)
                .toJobParameters();
        jobLauncher.run(blogVisitStatisticJob, jobParameters);
    }
}
