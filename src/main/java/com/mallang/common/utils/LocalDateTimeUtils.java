package com.mallang.common.utils;

import java.time.LocalDateTime;

public class LocalDateTimeUtils {

    public static LocalDateTime onlyHours(LocalDateTime source) {
        return source.withNano(0)
                .withSecond(0)
                .withMinute(0);
    }
}
