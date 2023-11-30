package com.mallang.statistics.api.query.repository;

import com.mallang.statistics.statistic.BlogVisitStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogVisitStatisticQueryRepository extends
        JpaRepository<BlogVisitStatistic, Long>,
        BlogVisitStatisticManageDao,
        BlogVisitStatisticSimpleDao {

}
