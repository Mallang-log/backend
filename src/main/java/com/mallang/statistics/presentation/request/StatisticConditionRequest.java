package com.mallang.statistics.presentation.request;

import com.mallang.statistics.query.support.PeriodType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record StatisticConditionRequest(
        @NotNull PeriodType periodType,
        @NotNull LocalDate lastDay,
        @NotNull int count
) {

    /**
     * DAY라면 그대로 WEEK 라면 이번주의 마지막 요일(토)이 lastDay가 되어야 함, start는 이번주의 시작(일) 기준으로 count 전주만큼이 되어야 함 MONTH라면 이번달의 마지막일 (30
     * 혹은 31)이 lastDay가 되어야 합 , start는 이번달의 시작(일) 기준으로 count 전달만큼이 되어야 함 YEAR 라면 올해의 마지막일 (12/31)이 lastDay가 되어야 함  ,
     * start는 이번년의 시작(일) 기준으로 count 전달만큼이 되어야 함
     *
     * @return
     */
    public LocalDate startDay() {
        lastDay.getDayOfWeek();
        return switch (periodType) {
            case DAY -> lastDay.minusDays(count);
            case WEEK -> lastDay.minusWeeks(count);
            case MONTH -> lastDay.minusMonths(count);
            case YEAR -> lastDay.minusYears(count);
        };
    }
}
