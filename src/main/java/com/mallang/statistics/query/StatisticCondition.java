package com.mallang.statistics.query;

import com.mallang.statistics.query.support.PeriodType;
import java.time.LocalDate;

public record StatisticCondition(
        PeriodType periodType,
        LocalDate lastDay,
        LocalDate startDay
) {

}
