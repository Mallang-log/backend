package com.mallang.statistics.api.query.repository;

import static com.mallang.statistics.statistic.QBlogVisitStatistic.blogVisitStatistic;

import com.mallang.blog.domain.Blog;
import com.mallang.statistics.api.query.StatisticQueryCondition;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public interface BlogVisitStatisticManageDao {

    List<BlogVisitStatistic> findByBlog(Blog blog, StatisticQueryCondition condition);

    @RequiredArgsConstructor
    @Component
    class BlogVisitStatisticManageDaoImpl implements BlogVisitStatisticManageDao {

        private final JPAQueryFactory query;

        @Override
        public List<BlogVisitStatistic> findByBlog(Blog blog, StatisticQueryCondition condition) {
            return query.selectFrom(blogVisitStatistic)
                    .where(
                            blogVisitStatistic.blogName.eq(blog.getName()),
                            blogVisitStatistic.statisticDate.between(
                                    condition.startDayInclude(),
                                    condition.lastDayInclude()
                            )
                    )
                    .orderBy(blogVisitStatistic.statisticDate.asc())
                    .fetch();
        }
    }
}
