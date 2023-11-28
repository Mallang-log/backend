package com.mallang.statistics.query.dto;

import com.mallang.statistics.query.support.PeriodType;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record PostViewStatisticQueryDto(
        Long memberId,
        String blogName,
        Long postId,
        PeriodType periodType,
        LocalDate lastDay,
        int count
) {
}
