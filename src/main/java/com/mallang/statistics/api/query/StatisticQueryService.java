package com.mallang.statistics.api.query;

import com.mallang.statistics.api.query.dao.PostViewStatisticDao;
import com.mallang.statistics.api.query.dto.PostViewStatisticQueryDto;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import com.mallang.statistics.api.query.support.StatisticConditionConverter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class StatisticQueryService {

    private final PostViewStatisticDao postViewStatisticDao;

    public List<PostViewStatisticResponse> getPostStatistics(PostViewStatisticQueryDto dto) {
        StatisticCondition cond = StatisticConditionConverter.convert(dto.periodType(), dto.lastDay(), dto.count());
        return postViewStatisticDao.find(dto.memberId(), dto.blogName(), dto.postId(), cond);
    }
}
