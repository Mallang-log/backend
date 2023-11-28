package com.mallang.statistics.statistic;

import com.mallang.post.domain.PostId;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewStatisticRepository extends JpaRepository<PostViewStatistic, Long> {

    Optional<PostViewStatistic> findByPostIdAndStatisticDate(PostId postId, LocalDate localDate);
}
