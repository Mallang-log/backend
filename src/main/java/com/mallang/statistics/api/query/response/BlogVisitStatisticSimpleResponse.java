package com.mallang.statistics.api.query.response;

import lombok.Builder;

@Builder
public record BlogVisitStatisticSimpleResponse(
        int totalVisitCount,
        int todayVisitCount,
        int yesterdayVisitCount
) {
}

