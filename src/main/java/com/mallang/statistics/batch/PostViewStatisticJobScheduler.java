package com.mallang.statistics.batch;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostViewStatisticJobScheduler {

    public static final String EACH_HOUR_CRON = "0 0 * * * *";

    private final JobLauncher jobLauncher;
    private final Job postViewStatisticJob;

    @SneakyThrows
    @Scheduled(cron = EACH_HOUR_CRON)
    public void runTask() {
        LocalDateTime now = LocalDateTime.now();
        log.info("포스트 조회수 통계 작업 실행 [실행시간: {}]", now);
        JobParameters params = new JobParametersBuilder()
                .addJobParameter("date", new JobParameter<>(now, LocalDateTime.class))
                .toJobParameters();
        jobLauncher.run(postViewStatisticJob, params);
    }
}
