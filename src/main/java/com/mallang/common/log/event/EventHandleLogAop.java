package com.mallang.common.log.event;

import com.mallang.common.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class EventHandleLogAop {

    @Pointcut("@annotation(org.springframework.context.event.EventListener)")
    public void eventListeners() {
    }

    @Pointcut("@annotation(org.springframework.transaction.event.TransactionalEventListener)")
    public void transactionalEventListeners() {
    }

    @Before("(eventListeners() || transactionalEventListeners()) && args(event)")
    public void handleEventLog(JoinPoint joinPoint, DomainEvent event) {
        String className = getClassSimpleName(joinPoint);
        String methodName = getMethodName(joinPoint);
        log.info("Handle [{}(Domain Id: {})] by [{}.{}()]",
                event.getClass().getSimpleName(),
                event.id(),
                className,
                methodName);
    }

    private String getClassSimpleName(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getTarget().getClass();
        return clazz.getSimpleName();
    }

    private String getMethodName(JoinPoint joinPoint) {
        return joinPoint.getSignature().getName();
    }
}
