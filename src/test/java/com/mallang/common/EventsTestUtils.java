package com.mallang.common;

import org.springframework.test.context.event.ApplicationEvents;

public class EventsTestUtils {

    public static int count(ApplicationEvents events, Class<?> eventClass) {
        return (int) events.stream(eventClass).count();
    }
}
