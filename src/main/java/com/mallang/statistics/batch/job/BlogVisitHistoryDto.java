package com.mallang.statistics.batch.job;

import java.time.LocalDate;

public record BlogVisitHistoryDto(
        String blogName,
        LocalDate date,
        int visitCount
) {
}
