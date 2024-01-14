package com.mallang.statistics.batch;

import com.mallang.statistics.batch.exeution.JobExecution;
import com.mallang.statistics.batch.exeution.JobHistoryRecorder;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PostViewStatisticJobSchedulerLegacy {

    public static final String EACH_HOUR_CRON = "0 0 * * * *";
    private final JobHistoryRecorder jobHistoryRecorder;
    private final PostViewStatisticJobLegacy postViewStatisticJobLegacy;

    @Scheduled(cron = EACH_HOUR_CRON)
    public void runTask() {
        LocalDateTime now = LocalDateTime.now();
        log.info("포스트 조회수 통계 작업 실행 [실행시간: {}]", now);
        JobExecution history = new JobExecution("postViewStatisticJob", now);
        LocalDateTime startInclude = now.minusHours(2);
        LocalDateTime endExclude = now.minusHours(1);
        jobHistoryRecorder.record(history,
                () -> postViewStatisticJobLegacy.postViewsAggregationJob(startInclude, endExclude));
    }
}
