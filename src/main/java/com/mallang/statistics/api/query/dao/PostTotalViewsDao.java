package com.mallang.statistics.api.query.dao;


import static com.mallang.statistics.statistic.QPostViewStatistic.postViewStatistic;

import com.mallang.post.domain.Post;
import com.mallang.post.query.support.PostQuerySupport;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostTotalViewsDao {

    private final PostQuerySupport postQuerySupport;
    private final JPAQueryFactory query;

    public PostTotalViewsResponse find(String blogName, Long postId) {
        Post post = postQuerySupport.getByIdAndBlogName(postId, blogName);
        int totalCount = query.selectFrom(postViewStatistic)
                .where(postViewStatistic.postId.eq(post.getPostId()))
                .fetch()
                .stream()
                .mapToInt(PostViewStatistic::getCount)
                .sum();
        return new PostTotalViewsResponse(totalCount);
    }
}
