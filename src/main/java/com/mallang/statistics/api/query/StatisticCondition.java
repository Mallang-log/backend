package com.mallang.statistics.api.query;

import com.mallang.statistics.api.query.support.PeriodType;
import java.time.LocalDate;

public record StatisticCondition(
        PeriodType periodType,
        LocalDate startDayInclude,
        LocalDate lastDayInclude
) {

}
