package com.mallang.statistics.api.query.dao;

import static com.mallang.statistics.statistic.QBlogVisitStatistic.blogVisitStatistic;

import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogVisitStatisticSimpleDao {

    private final JPAQueryFactory query;

    public BlogVisitStatisticSimpleResponse find(String blogName, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);
        Map<LocalDate, BlogVisitStatistic> result = query.selectFrom(blogVisitStatistic)
                .where(
                        blogVisitStatistic.blogName.eq(blogName),
                        blogVisitStatistic.statisticDate.in(today, yesterday)
                )
                .orderBy(blogVisitStatistic.statisticDate.asc())
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        BlogVisitStatistic::getStatisticDate,
                        Function.identity())
                );
        int totalCount = query.selectFrom(blogVisitStatistic)
                .where(blogVisitStatistic.blogName.eq(blogName))
                .fetch()
                .stream()
                .mapToInt(BlogVisitStatistic::getCount)
                .sum();
        int todayCount = result.get(today) == null ? 0 : result.get(today).getCount();
        int yesterdayCount = result.get(yesterday) == null ? 0 : result.get(yesterday).getCount();
        return new BlogVisitStatisticSimpleResponse(
                totalCount,
                todayCount,
                yesterdayCount
        );
    }
}

