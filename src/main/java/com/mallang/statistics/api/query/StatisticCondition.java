package com.mallang.statistics.api.query;

import java.time.LocalDate;

public record StatisticCondition(
        PeriodType periodType,
        LocalDate startDayInclude,
        LocalDate lastDayInclude
) {
}
