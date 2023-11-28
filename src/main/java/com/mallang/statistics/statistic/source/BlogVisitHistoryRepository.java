package com.mallang.statistics.statistic.source;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogVisitHistoryRepository extends JpaRepository<BlogVisitHistory, Long> {

    Optional<BlogVisitHistory> findFirstByUuidAndBlogNameOrderByCreatedDateDesc(UUID uuid, String blogName);
}
