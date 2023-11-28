package com.mallang.statistics.statistic.utils;

import java.time.LocalDateTime;

public final class LocalDateTimeUtils {

    private LocalDateTimeUtils() {
    }

    public static LocalDateTime nowWithoutSeconds() {
        return LocalDateTime.now()
                .withNano(0)
                .withSecond(0);
    }
}
