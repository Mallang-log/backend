package com.mallang.statistics.statistic;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogVisitStatisticRepository extends JpaRepository<BlogVisitStatistic, Long> {

    Optional<BlogVisitStatistic> findByBlogNameAndStatisticDate(String blogName, LocalDate date);
}
