package com.mallang.statistics.api.query.dao;

import static com.mallang.statistics.statistic.QBlogVisitStatistic.blogVisitStatistic;

import com.mallang.blog.domain.Blog;
import com.mallang.blog.query.support.BlogQuerySupport;
import com.mallang.statistics.api.query.StatisticCondition;
import com.mallang.statistics.api.query.response.BlogVisitStatisticManageResponse;
import com.mallang.statistics.api.query.support.PeriodPartitioner;
import com.mallang.statistics.api.query.support.PeriodPartitioner.PeriodPart;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.utils.LocalDateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BlogVisitStatisticManageDao {

    private final BlogQuerySupport blogQuerySupport;
    private final JPAQueryFactory query;

    public List<BlogVisitStatisticManageResponse> find(
            Long memberId,
            String blogName,
            StatisticCondition condition
    ) {
        Blog blog = blogQuerySupport.getByMemberAndBlog(memberId, blogName);
        List<BlogVisitStatistic> result = query.selectFrom(blogVisitStatistic)
                .where(
                        blogVisitStatistic.blogName.eq(blog.getName()),
                        blogVisitStatistic.statisticDate
                                .between(condition.startDayInclude(), condition.lastDayInclude())
                )
                .orderBy(blogVisitStatistic.statisticDate.asc())
                .fetch();
        List<BlogVisitStatisticManageResponse> response = getResponseTemplates(condition);
        aggregate(new ArrayDeque<>(result), response);
        return response;
    }

    private List<BlogVisitStatisticManageResponse> getResponseTemplates(StatisticCondition condition) {
        List<PeriodPart> partition = PeriodPartitioner
                .partition(condition.periodType(), condition.startDayInclude(), condition.lastDayInclude());
        return partition.stream()
                .map(it -> new BlogVisitStatisticManageResponse(it.startInclude(), it.endInclude()))
                .toList();
    }

    private void aggregate(Deque<BlogVisitStatistic> statistics, List<BlogVisitStatisticManageResponse> response) {
        for (BlogVisitStatisticManageResponse postViewStatisticResponse : response) {
            LocalDate startDate = postViewStatisticResponse.getStartDateInclude();
            LocalDate endDate = postViewStatisticResponse.getEndDateInclude();
            while (!statistics.isEmpty()) {
                BlogVisitStatistic postView = statistics.peekFirst();
                if (!LocalDateUtils.isBetween(startDate, endDate, postView.getStatisticDate())) {
                    break;
                }
                postViewStatisticResponse.addVisitCount(postView.getCount());
                statistics.pollFirst();
            }
            if (statistics.isEmpty()) {
                return;
            }
        }
    }
}
