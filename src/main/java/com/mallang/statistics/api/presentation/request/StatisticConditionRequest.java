package com.mallang.statistics.api.presentation.request;

import com.mallang.statistics.api.presentation.support.StatisticQueryConditionConverter;
import com.mallang.statistics.api.query.PeriodType;
import com.mallang.statistics.api.query.StatisticQueryCondition;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record StatisticConditionRequest(
        @NotNull PeriodType periodType,
        @NotNull LocalDate lastDay,
        @Min(1) int count
) {
    public StatisticQueryCondition toCondition() {
        return StatisticQueryConditionConverter.convert(periodType, lastDay, count);
    }
}
