package com.mallang.statistics.api.query.repository;

import static com.mallang.statistics.statistic.QPostViewStatistic.postViewStatistic;

import com.mallang.post.domain.Post;
import com.mallang.statistics.api.query.StatisticQueryCondition;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public interface PostViewStatisticDao {

    List<PostViewStatistic> findByPost(
            Post post,
            StatisticQueryCondition condition
    );

    @RequiredArgsConstructor
    @Component
    class PostViewStatisticDaoImpl implements PostViewStatisticDao {

        private final JPAQueryFactory query;

        @Override
        public List<PostViewStatistic> findByPost(Post post, StatisticQueryCondition condition) {
            return query.selectFrom(postViewStatistic)
                    .where(
                            postViewStatistic.postId.eq(post.getPostId()),
                            postViewStatistic.statisticDate.between(condition.startDayInclude(),
                                    condition.lastDayInclude())
                    )
                    .orderBy(postViewStatistic.statisticDate.asc())
                    .fetch();
        }
    }
}
