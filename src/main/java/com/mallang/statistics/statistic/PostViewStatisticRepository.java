package com.mallang.statistics.statistic;

import com.mallang.post.domain.PostId;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostViewStatisticRepository extends JpaRepository<PostViewStatistic, Long> {

    Optional<PostViewStatistic> findByPostIdAndStatisticDate(PostId postId, LocalDate localDate);

    @Modifying
    @Query("DELETE FROM PostViewStatistic s WHERE s.postId = :postId")
    void deleteAllByPostId(PostId postId);
}
