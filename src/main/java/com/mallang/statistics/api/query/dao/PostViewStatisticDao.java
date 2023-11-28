package com.mallang.statistics.api.query.dao;

import static com.mallang.statistics.statistic.QPostViewStatistic.postViewStatistic;

import com.mallang.post.domain.Post;
import com.mallang.post.query.support.PostQuerySupport;
import com.mallang.statistics.api.query.StatisticCondition;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import com.mallang.statistics.api.query.support.PeriodPartitioner;
import com.mallang.statistics.api.query.support.PeriodPartitioner.PeriodPart;
import com.mallang.statistics.statistic.PostViewStatistic;
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
public class PostViewStatisticDao {

    private final PostQuerySupport postQuerySupport;
    private final JPAQueryFactory query;

    public List<PostViewStatisticResponse> find(
            Long memberId,
            String blogName,
            Long postId,
            StatisticCondition condition
    ) {
        Post post = postQuerySupport.getByPostIdAndBlogNameAndWriterId(postId, blogName, memberId);
        List<PostViewStatistic> result = query.selectFrom(postViewStatistic)
                .where(
                        postViewStatistic.postId.eq(post.getPostId()),
                        postViewStatistic.statisticDate.between(condition.startDayInclude(), condition.lastDayInclude())
                )
                .orderBy(postViewStatistic.statisticDate.asc())
                .fetch();
        List<PostViewStatisticResponse> response = getResponseTemplates(condition);
        aggregate(new ArrayDeque<>(result), response);
        return response;
    }

    private List<PostViewStatisticResponse> getResponseTemplates(StatisticCondition condition) {
        List<PeriodPart> partition = PeriodPartitioner
                .partition(condition.periodType(), condition.startDayInclude(), condition.lastDayInclude());
        return partition.stream()
                .map(it -> new PostViewStatisticResponse(it.startInclude(), it.endInclude()))
                .toList();
    }

    private void aggregate(Deque<PostViewStatistic> statistics, List<PostViewStatisticResponse> response) {
        for (PostViewStatisticResponse postViewStatisticResponse : response) {
            LocalDate startDate = postViewStatisticResponse.getStartDateInclude();
            LocalDate endDate = postViewStatisticResponse.getEndDateInclude();
            while (!statistics.isEmpty()) {
                PostViewStatistic postView = statistics.peekFirst();
                if (!LocalDateUtils.isBetween(startDate, endDate, postView.getStatisticDate())) {
                    break;
                }
                postViewStatisticResponse.addViewCount(postView.getCount());
                statistics.pollFirst();
            }
            if (statistics.isEmpty()) {
                return;
            }
        }
    }
}
