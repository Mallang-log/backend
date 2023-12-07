package com.mallang.common.utils;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

public final class ObjectsUtils {

    private ObjectsUtils() {
    }

    public static <T> void validateWhenNonNullWithFailCond(
            T object,
            Predicate<T> failCond,
            RuntimeException exception
    ) {
        if (object == null) {
            return;
        }
        if (failCond.test(object)) {
            throw exception;
        }
    }

    public static boolean isNulls(Object... objects) {
        return Arrays.stream(objects)
                .allMatch(Objects::isNull);
    }

    public static boolean notEquals(Object a, Object b) {
        return !Objects.equals(a, b);
    }
}
