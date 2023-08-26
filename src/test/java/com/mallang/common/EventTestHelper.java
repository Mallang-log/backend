package com.mallang.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
@Component
public class EventTestHelper {

    private final Map<Class<?>, Integer> store = new HashMap<>();

    @EventListener
    public void listen(Object event) {
        store.put(event.getClass(), store.getOrDefault(event.getClass(), 0) + 1);
    }

    public int 이벤트_발생_횟수(Class<?> eventClass) {
        return store.get(eventClass);
    }
}
