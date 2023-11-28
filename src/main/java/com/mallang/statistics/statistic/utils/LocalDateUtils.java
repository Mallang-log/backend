package com.mallang.statistics.statistic.utils;

import java.time.LocalDate;

public final class LocalDateUtils {

    private LocalDateUtils() {
    }

    public static boolean isBetween(LocalDate startInclude, LocalDate endInclude, LocalDate target) {
        return (startInclude.isBefore(target) || startInclude.isEqual(target))
                && (endInclude.isAfter(target) || endInclude.isEqual(target));
    }
}
