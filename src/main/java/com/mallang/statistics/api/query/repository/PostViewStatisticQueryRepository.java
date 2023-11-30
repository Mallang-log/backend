package com.mallang.statistics.api.query.repository;

import com.mallang.post.domain.PostId;
import com.mallang.statistics.statistic.PostViewStatistic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewStatisticQueryRepository extends
        JpaRepository<PostViewStatistic, Long>,
        PostViewStatisticDao {

    List<PostViewStatistic> findAllByPostId(PostId postId);
}
