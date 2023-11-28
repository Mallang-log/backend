package com.mallang.statistics.query.support;

import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.lastDayOfYear;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

import com.mallang.statistics.query.StatisticCondition;
import java.time.LocalDate;

public final class DateConverter {

    public static StatisticCondition convert(
            PeriodType periodType,
            LocalDate lastDay,
            int count
    ) {
        LocalDate lastDateOfPeriod = getLastDate(lastDay, periodType);
        LocalDate startDataOfPeriod = getStartData(lastDateOfPeriod, periodType, count);
        return new StatisticCondition(periodType, lastDateOfPeriod, startDataOfPeriod);
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
        LocalDate startDateOfLastDateOfPeriod = getStartDateOfLastDateOfPeriod(lastDateOfPeriod, periodType);
        return startDateOfLastDateOfPeriod.minus(count, periodType.temporalUnit());
    }

    private static LocalDate getStartDateOfLastDateOfPeriod(LocalDate lastDateOfPeriod, PeriodType periodType) {
        return switch (periodType) {
            case DAY -> lastDateOfPeriod;
            case WEEK -> lastDateOfPeriod.with(previousOrSame(MONDAY));
            case MONTH -> lastDateOfPeriod.withDayOfMonth(1);
            case YEAR -> lastDateOfPeriod.withDayOfYear(1);
        };
    }
}
