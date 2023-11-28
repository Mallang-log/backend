package com.mallang.statistics.api.presentation.request;

import com.mallang.statistics.api.query.PeriodType;
import com.mallang.statistics.api.query.dto.BlogVisitStatisticManageQueryDto;
import com.mallang.statistics.api.query.dto.PostViewStatisticQueryDto;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record StatisticConditionRequest(
        @NotNull PeriodType periodType,
        @NotNull LocalDate lastDay,
        int count
) {

    public BlogVisitStatisticManageQueryDto toBlogVisitStatisticManageQueryDto(Long memberId,
                                                                               String blogName) {
        return BlogVisitStatisticManageQueryDto.builder()
                .memberId(memberId)
                .blogName(blogName)
                .periodType(periodType)
                .lastDay(lastDay)
                .count(count)
                .build();
    }

    public PostViewStatisticQueryDto toPostViewStatisticQueryDto(Long memberId,
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
