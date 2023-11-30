package com.mallang.statistics.api.presentation.support;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import com.mallang.statistics.api.query.PeriodType;
import com.mallang.statistics.api.query.StatisticQueryCondition;
import java.time.LocalDate;

public final class StatisticQueryConditionConverter {

    private StatisticQueryConditionConverter() {
    }

    public static StatisticQueryCondition convert(
            PeriodType periodType,
            LocalDate lastDay,
            int count
    ) {
        if (count < 1) {
            throw new IllegalArgumentException("조회 개수는 0개 이상이어야 합니다.");
        }
        LocalDate lastDateOfPeriod = getLastDate(lastDay, periodType);
        LocalDate startDataOfPeriod = getStartData(lastDateOfPeriod, periodType, count);
        return new StatisticQueryCondition(periodType, startDataOfPeriod, lastDateOfPeriod);
    }

    private static LocalDate getLastDate(LocalDate lastDay, PeriodType periodType) {
        return switch (periodType) {
            case DAY -> lastDay;
            case WEEK -> lastDay.plusDays((long) SUNDAY.getValue() - lastDay.getDayOfWeek().getValue());
            case MONTH -> lastDay.with(lastDayOfMonth());
            case YEAR -> lastDay.with(lastDayOfYear());
        };
    }

    private static LocalDate getStartData(LocalDate lastDateOfPeriod, PeriodType periodType, int count) {
        LocalDate startDateOfLastDateOfPeriod = getStartDateOfLastPeriod(lastDateOfPeriod, periodType);
        return startDateOfLastDateOfPeriod.minus((long) count - 1, periodType.temporalUnit());
    }

    private static LocalDate getStartDateOfLastPeriod(LocalDate lastDateOfPeriod, PeriodType periodType) {
        return switch (periodType) {
            case DAY -> lastDateOfPeriod;
            case WEEK -> lastDateOfPeriod.with(previousOrSame(MONDAY));
            case MONTH -> lastDateOfPeriod.withDayOfMonth(1);
            case YEAR -> lastDateOfPeriod.withDayOfYear(1);
        };
    }
}
