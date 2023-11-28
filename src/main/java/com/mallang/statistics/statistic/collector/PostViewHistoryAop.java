package com.mallang.statistics.statistic.collector;

import com.mallang.post.domain.PostId;
import com.mallang.post.query.response.PostDetailResponse;
import com.mallang.statistics.statistic.source.PostViewHistory;
import com.mallang.statistics.statistic.utils.HttpUtils;
import com.mallang.statistics.statistic.utils.LocalDateTimeUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Aspect
public class PostViewHistoryAop {

    private static final String POST_VIEW_COOKIE = "POST_VIEW_COOKIE";
    private static final int NO_EXPIRED_MAX_AGE = 60 * 60 * 24 * 365 * 10;

    private final PostViewHistoryCollector postViewHistoryCollector;

    @AfterReturning(
            pointcut = "com.mallang.statistics.statistic.collector.PostViewHistoryAop.PointCuts.postQueryServiceGetByIdAndBlogName()",
            returning = "result"
    )
    public void history(PostDetailResponse result) {
        UUID uuid = getPostViewCookieValue();
        LocalDateTime now = LocalDateTimeUtils.nowWithoutSeconds();
        PostId postId = new PostId(result.postId(), result.blogId());
        postViewHistoryCollector.save(new PostViewHistory(uuid, postId, now));
    }

    private UUID getPostViewCookieValue() {
        return Stream.of(HttpUtils.getRequestCookies())
                .filter(cookie -> cookie.getName().equals(POST_VIEW_COOKIE))
                .map(Cookie::getValue)
                .map(UUID::fromString)
                .findAny()
                .orElseGet(this::generatePostViewCookieValue);
    }

    private UUID generatePostViewCookieValue() {
        HttpServletResponse response = HttpUtils.getHttpServletResponse();
        UUID uuid = UUID.randomUUID();
        Cookie cookie = new Cookie(POST_VIEW_COOKIE, uuid.toString());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(NO_EXPIRED_MAX_AGE);
        response.addCookie(cookie);
        return uuid;
    }

    public static class PointCuts {

        @Pointcut("execution(* com.mallang.post.query.PostQueryService.getByIdAndBlogName(..))")
        void postQueryServiceGetByIdAndBlogName() {
        }
    }
}
