package com.mallang.statistics.query.support;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public enum PeriodType {

    DAY(ChronoUnit.DAYS),
    WEEK(ChronoUnit.WEEKS),
    MONTH(ChronoUnit.MONTHS),
    YEAR(ChronoUnit.YEARS);

    private final TemporalUnit temporalUnit;

    PeriodType(TemporalUnit temporalUnit) {
        this.temporalUnit = temporalUnit;
    }

    public TemporalUnit temporalUnit() {
        return temporalUnit;
    }
}

