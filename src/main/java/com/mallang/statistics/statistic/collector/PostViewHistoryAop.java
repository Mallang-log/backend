package com.mallang.statistics.statistic.collector;

import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.statistics.statistic.source.PostViewHistory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RequiredArgsConstructor
@Component
@Aspect
public class PostViewHistoryAop {

    private static final String POST_VIEW_COOKIE = "POST_VIEW_COOKIE";
    private static final int NO_EXPIRED_MAX_AGE = 60 * 60 * 24 * 365 * 10;

    private final PostViewHistoryCollector postViewHistoryCollector;

    @Pointcut("execution(* com.mallang.post.query.PostQueryService.getByIdAndBlogName(..))")
    public void postQueryServiceGetByIdAndBlogName() {
    }

    @AfterReturning(
            pointcut = "postQueryServiceGetByIdAndBlogName()",
            returning = "result"
    )
    public void history(PostDetailResponse result) {
        UUID uuid = getPostViewCookieValue();
        LocalDateTime now = LocalDateTime.now()
                .withNano(0)
                .withSecond(0);
        postViewHistoryCollector.save(new PostViewHistory(uuid, result.id(), now));
    }

    private UUID getPostViewCookieValue() {
        return Stream.of(getRequestCookies())
                .filter(cookie -> cookie.getName().equals(POST_VIEW_COOKIE))
                .map(Cookie::getValue)
                .map(UUID::fromString)
                .findAny()
                .orElseGet(this::generatePostViewCookieValue);
    }

    private Cookie[] getRequestCookies() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (request.getCookies() == null) {
            return new Cookie[]{};
        }
        return request.getCookies();
    }

    private UUID generatePostViewCookieValue() {
        UUID uuid = UUID.randomUUID();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        Objects.requireNonNull(response);
        Cookie cookie = new Cookie(POST_VIEW_COOKIE, uuid.toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(NO_EXPIRED_MAX_AGE);
        response.addCookie(cookie);
        return uuid;
    }
}
