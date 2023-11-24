package com.mallang.statistics.statistic;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewStatisticRepository extends JpaRepository<PostViewStatistic, Long> {
    
    Optional<PostViewStatistic> findByPostIdAndStatisticDate(Long postId, LocalDate localDate);
}