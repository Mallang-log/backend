package com.mallang.statistics.batch.exeution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobExecutionRepository extends JpaRepository<JobExecution, Long> {
}
