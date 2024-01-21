package com.mallang.statistics.statistic.source;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogVisitHistoryRepository extends JpaRepository<BlogVisitHistory, Long> {

    Optional<BlogVisitHistory> findFirstByUuidAndBlogNameOrderByCreatedDateDesc(UUID uuid, String blogName);

    @Modifying
    @Query("DELETE FROM BlogVisitHistory h WHERE h.createdDate >= :startInclude AND h.createdDate < :endExclude")
    void deleteWithCreatedDateBetweenIncludeStartAndExcludeEnd(
            @Param("startInclude") LocalDateTime startInclude,
            @Param("endExclude") LocalDateTime endExclude
    );
}
