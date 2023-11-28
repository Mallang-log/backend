package com.mallang.statistics.api.presentation.request;

import com.mallang.statistics.api.query.dto.PostViewStatisticQueryDto;
import com.mallang.statistics.api.query.support.PeriodType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record StatisticConditionRequest(
        @NotNull PeriodType periodType,
        @NotNull LocalDate lastDay,
        int count
) {

    public PostViewStatisticQueryDto toDto(Long memberId,
                                           String blogName,
                                           Long postId) {
        return PostViewStatisticQueryDto.builder()
                .memberId(memberId)
                .blogName(blogName)
                .postId(postId)
                .periodType(periodType)
                .lastDay(lastDay)
                .count(count)
                .build();
    }
}
