package com.mallang.statistics.api.query.support;

import com.mallang.statistics.api.query.PeriodType;
import com.mallang.statistics.api.query.StatisticQueryCondition;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class PeriodPartitioner {

    private PeriodPartitioner() {
    }

    public static Stream<PeriodPart> partition(StatisticQueryCondition condition) {
        return partition(
                condition.periodType(),
                condition.startDayInclude(),
                condition.lastDayInclude()
        ).stream();
    }

    private static List<PeriodPart> partition(
            PeriodType periodType,
            LocalDate startDayInclude,
            LocalDate lastDayInclude
    ) {
        List<PeriodPart> parts = new ArrayList<>();
        LocalDate current = startDayInclude;
        while (current.isBefore(lastDayInclude) || current.isEqual(lastDayInclude)) {
            LocalDate next = current.plus(1, periodType.temporalUnit());
            parts.add(new PeriodPart(current, next.minusDays(1)));
            current = next;
        }
        return parts;
    }

    public record PeriodPart(
            LocalDate startInclude,
            LocalDate endInclude
    ) {
    }
}
