package com.mallang.statistics.job;

import com.mallang.statistics.job.exeution.JobExecution;
import com.mallang.statistics.job.exeution.JobHistoryRecorder;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BlogVisitStatisticJobScheduler {

    public static final String EACH_HOUR_CRON = "0 0 * * * *";
    private final JobHistoryRecorder jobHistoryRecorder;
    private final BlogVisitStatisticJob blogVisitStatisticJob;

    @Scheduled(cron = EACH_HOUR_CRON)
    public void runTask() {
        LocalDateTime now = LocalDateTime.now();
        log.info("블로그 방문수 통계 작업 실행 [실행시간: {}]", now);
        JobExecution history = new JobExecution("blogVisitsAggregationJob", now);
        jobHistoryRecorder.record(history, blogVisitStatisticJob::blogVisitsAggregationJob);
    }
}
