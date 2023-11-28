package com.mallang.statistics.query.dao;

import static com.mallang.post.domain.QPost.post;
import static com.mallang.statistics.statistic.QPostViewStatistic.postViewStatistic;

import com.mallang.post.domain.Post;
import com.mallang.statistics.query.StatisticCondition;
import com.mallang.statistics.query.response.PostViewStatisticResponse;
import com.mallang.statistics.statistic.utils.LocalDateUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostViewStatisticDao {

    private final JPAQueryFactory query;

    public List<PostViewStatisticResponse> find(
            Long memberId,
            String blogName,
            Long postId,
            StatisticCondition condition
    ) {
        List<PostViewStatisticResponse> response = getResponseTemplates(condition);
        Post findPost = query.selectFrom(post)
                .where(
                        post.postId.id.eq(postId),
                        post.blog.name.value.eq(blogName),
                        post.writer.id.eq(memberId)
                )
                .fetchFirst();
        query.selectFrom(postViewStatistic)
                .where(
                        postViewStatistic.postId.eq(findPost.getPostId()),
                        postViewStatistic.statisticDate.between(condition.startDayInclude(), condition.lastDayInclude())
                ).fetch()
                .forEach(viewStatistic -> {
                    for (PostViewStatisticResponse postViewStatisticResponse : response) {
                        LocalDate statisticDate = viewStatistic.getStatisticDate();
                        LocalDate startDate = postViewStatisticResponse.getStartDateInclude();
                        LocalDate endDate = postViewStatisticResponse.getEndDateInclude();
                        if (LocalDateUtils.isBetween(startDate, endDate, statisticDate)) {
                            postViewStatisticResponse.addViewCount(viewStatistic.getCount());
                            break;
                        }
                    }
                });
        return response;
    }

    private List<PostViewStatisticResponse> getResponseTemplates(StatisticCondition condition) {
        List<PostViewStatisticResponse> postViewStatisticResponses = new ArrayList<>();
        LocalDate current = condition.startDayInclude();
        while (current.isBefore(condition.lastDayInclude()) || current.isEqual(condition.lastDayInclude())) {
            LocalDate next = current
                    .plus(1, condition.periodType().temporalUnit());
            postViewStatisticResponses.add(new PostViewStatisticResponse(current, next.minusDays(1)));
            current = next;
        }
        return postViewStatisticResponses;
    }
}
