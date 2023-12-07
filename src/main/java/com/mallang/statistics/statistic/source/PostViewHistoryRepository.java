package com.mallang.statistics.statistic.source;

import com.mallang.post.domain.PostId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostViewHistoryRepository extends JpaRepository<PostViewHistory, Long> {

    Optional<PostViewHistory> findFirstByUuidAndPostIdOrderByCreatedDateDesc(UUID uuid, PostId postId);

    @Query("SELECT h FROM PostViewHistory h WHERE h.createdDate >= :startInclude AND h.createdDate < :endExclude")
    List<PostViewHistory> findWithCreatedDateBetweenIncludeStartAndExcludeEnd(
            @Param("startInclude") LocalDateTime startInclude,
            @Param("endExclude") LocalDateTime endExclude
    );
}
