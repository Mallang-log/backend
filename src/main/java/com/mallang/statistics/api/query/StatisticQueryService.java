package com.mallang.statistics.api.query;

import com.mallang.statistics.api.query.dao.BlogVisitStatisticManageDao;
import com.mallang.statistics.api.query.dao.BlogVisitStatisticSimpleDao;
import com.mallang.statistics.api.query.dao.PostTotalViewsDao;
import com.mallang.statistics.api.query.dao.PostViewStatisticDao;
import com.mallang.statistics.api.query.dto.BlogVisitStatisticManageQueryDto;
import com.mallang.statistics.api.query.dto.PostViewStatisticQueryDto;
import com.mallang.statistics.api.query.response.BlogVisitStatisticManageResponse;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import com.mallang.statistics.api.query.support.StatisticConditionConverter;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class StatisticQueryService {

    private final BlogVisitStatisticSimpleDao blogVisitStatisticSimpleDao;
    private final BlogVisitStatisticManageDao blogVisitStatisticManageDao;
    private final PostViewStatisticDao postViewStatisticDao;
    private final PostTotalViewsDao postTotalViewsDao;

    public BlogVisitStatisticSimpleResponse getSimpleBlogVisitStatistics(String blogName, LocalDate today) {
        return blogVisitStatisticSimpleDao.find(blogName, today);
    }

    public PostTotalViewsResponse getPostTotalViews(String blogName, Long postId) {
        return postTotalViewsDao.find(blogName, postId);
    }

    public List<BlogVisitStatisticManageResponse> getBlogVisitStatistics(BlogVisitStatisticManageQueryDto dto) {
        StatisticCondition cond = StatisticConditionConverter.convert(dto.periodType(), dto.lastDay(), dto.count());
        return blogVisitStatisticManageDao.find(dto.memberId(), dto.blogName(), cond);
    }

    public List<PostViewStatisticResponse> getPostViewStatistics(PostViewStatisticQueryDto dto) {
        StatisticCondition cond = StatisticConditionConverter.convert(dto.periodType(), dto.lastDay(), dto.count());
        return postViewStatisticDao.find(dto.memberId(), dto.blogName(), dto.postId(), cond);
    }
}
