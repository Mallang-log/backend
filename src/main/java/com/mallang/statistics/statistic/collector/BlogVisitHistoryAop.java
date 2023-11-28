package com.mallang.statistics.statistic.collector;

import com.mallang.statistics.statistic.source.BlogVisitHistory;
import com.mallang.statistics.statistic.utils.HttpUtils;
import com.mallang.statistics.statistic.utils.LocalDateTimeUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class BlogVisitHistoryAop {

    private static final String BLOG_VISIT_COOKIE = "BLOG_VISIT_COOKIE";
    private static final int NO_EXPIRED_MAX_AGE = 60 * 60 * 24 * 365 * 10;

    private final BlogVisitHistoryCollector blogVisitHistoryCollector;

    @AfterReturning(pointcut = "com.mallang.statistics.statistic.collector.BlogVisitHistoryAop.PointCuts.blogVisitControllerMethods()")
    public void history(JoinPoint joinPoint) {
        getBlogName(joinPoint).ifPresent(blogName -> {
            UUID uuid = getBlogVisitCookieValue();
            LocalDateTime now = LocalDateTimeUtils.nowWithoutSeconds();
            HttpServletRequest request = HttpUtils.getHttpServletRequest();
            String ipAddress = request.getRemoteAddr();
            String originHeader = request.getHeader("Origin");
            BlogVisitHistory blogVisitHistory = new BlogVisitHistory(uuid, blogName, originHeader, ipAddress, now);
            blogVisitHistoryCollector.save(blogVisitHistory);
        });
    }

    private Optional<String> getBlogName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            if (parameterNames[i].equals("blogName")) {
                return Optional.ofNullable((String) args[i]);
            }
        }
        return Optional.empty();
    }

    private UUID getBlogVisitCookieValue() {
        return Stream.of(HttpUtils.getRequestCookies())
                .filter(cookie -> cookie.getName().equals(BLOG_VISIT_COOKIE))
                .map(Cookie::getValue)
                .map(UUID::fromString)
                .findAny()
                .orElseGet(this::generatePostViewCookieValue);
    }

    private UUID generatePostViewCookieValue() {
        HttpServletResponse response = HttpUtils.getHttpServletResponse();
        UUID uuid = UUID.randomUUID();
        Cookie cookie = new Cookie(BLOG_VISIT_COOKIE, uuid.toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(NO_EXPIRED_MAX_AGE);
        response.addCookie(cookie);
        return uuid;
    }

    public static class PointCuts {

        @Pointcut("within(com.mallang.blog.presentation.BlogController)")
        void blogController() {
        }

        @Pointcut("within(com.mallang.blog.presentation.AboutController)")
        void aboutController() {
        }

        @Pointcut("within(com.mallang.post.presentation.PostController)")
        void postController() {
        }

        @Pointcut("blogController() || aboutController() || postController()")
        void blogVisitController() {
        }

        @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
        void getMappingMethods() {
        }

        @Pointcut("blogVisitController() && getMappingMethods()")
        void blogVisitControllerMethods() {
        }
    }
}
