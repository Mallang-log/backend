package com.mallang.statistics.statistic.source;

import com.mallang.post.domain.PostId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewHistoryRepository extends JpaRepository<PostViewHistory, Long> {


    Optional<PostViewHistory> findFirstByUuidAndPostIdOrderByCreatedDateDesc(UUID uuid, PostId postId);
}
