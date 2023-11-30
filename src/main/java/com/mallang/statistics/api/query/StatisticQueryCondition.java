package com.mallang.statistics.api.query;

import java.time.LocalDate;

public record StatisticQueryCondition(
        PeriodType periodType,
        LocalDate startDayInclude,
        LocalDate lastDayInclude
) {
}
