package com.mallang.statistics.api.query.dto;

import com.mallang.statistics.api.query.PeriodType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record BlogVisitStatisticManageQueryDto(
        Long memberId,
        String blogName,
        PeriodType periodType,
        LocalDate lastDay,
        int count
) {
}
