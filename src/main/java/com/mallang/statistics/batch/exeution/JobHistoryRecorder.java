package com.mallang.statistics.batch.exeution;

import static com.mallang.statistics.batch.exeution.Status.SUCCESS;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JobHistoryRecorder {

    private final JobExecutionRepository jobExecutionRepository;

    public void record(JobExecution history, Runnable runnable) {
        jobExecutionRepository.saveAndFlush(history);
        try {
            runnable.run();
            history.setStatus(SUCCESS);
        } catch (Exception e) {
            history.fail(e.getMessage());
        } finally {
            jobExecutionRepository.saveAndFlush(history);
        }
    }
}
