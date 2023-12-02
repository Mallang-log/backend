package com.mallang.statistics.api.query;

import com.mallang.auth.domain.Member;
import com.mallang.auth.query.repository.MemberQueryRepository;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.query.repository.BlogQueryRepository;
import com.mallang.post.domain.Post;
import com.mallang.post.query.repository.PostQueryRepository;
import com.mallang.statistics.api.query.repository.BlogVisitStatisticQueryRepository;
import com.mallang.statistics.api.query.repository.PostViewStatisticQueryRepository;
import com.mallang.statistics.api.query.response.BlogVisitStatisticManageResponse;
import com.mallang.statistics.api.query.response.BlogVisitStatisticSimpleResponse;
import com.mallang.statistics.api.query.response.CommonStatisticResponse;
import com.mallang.statistics.api.query.response.PostTotalViewsResponse;
import com.mallang.statistics.api.query.response.PostViewStatisticResponse;
import com.mallang.statistics.api.query.support.PeriodPartitioner;
import com.mallang.statistics.statistic.BlogVisitStatistic;
import com.mallang.statistics.statistic.CommonStatistic;
import com.mallang.statistics.statistic.PostViewStatistic;
import com.mallang.statistics.statistic.utils.LocalDateUtils;
import java.time.LocalDate;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class StatisticQueryService {

    private final MemberQueryRepository memberQueryRepository;
    private final BlogQueryRepository blogQueryRepository;
    private final PostQueryRepository postQueryRepository;
    private final BlogVisitStatisticQueryRepository blogVisitStatisticQueryRepository;
    private final PostViewStatisticQueryRepository postViewStatisticQueryRepository;

    public BlogVisitStatisticSimpleResponse getSimpleBlogVisitStatistics(String blogName, LocalDate today) {
        return blogVisitStatisticQueryRepository.getSimpleBlogVisitStatistics(blogName, today);
    }

    public PostTotalViewsResponse getPostTotalViews(String blogName, Long postId) {
        Post post = postQueryRepository.getById(postId, blogName);
        int totalViewCount = postViewStatisticQueryRepository.findAllByPostId(post.getId())
                .stream()
                .mapToInt(PostViewStatistic::getCount)
                .sum();
        return new PostTotalViewsResponse(totalViewCount);
    }

    public List<BlogVisitStatisticManageResponse> getBlogVisitStatistics(
            Long memberId,
            String blogName,
            StatisticQueryCondition cond
    ) {
        Blog blog = blogQueryRepository.getByName(blogName);
        Member member = memberQueryRepository.getById(memberId);
        blog.validateOwner(member);
        List<BlogVisitStatistic> blogVisitStatistics = blogVisitStatisticQueryRepository.findByBlog(blog, cond);
        List<BlogVisitStatisticManageResponse> responses = PeriodPartitioner.partition(cond)
                .map(BlogVisitStatisticManageResponse::from)
                .toList();
        accumulate(
                blogVisitStatistics,
                responses,
                (statistic, response) -> response.addVisitCount(statistic.getCount())
        );
        return responses;
    }

    public List<PostViewStatisticResponse> getPostViewStatistics(
            Long memberId,
            String blogName,
            Long postId,
            StatisticQueryCondition cond
    ) {
        Post post = postQueryRepository.getById(postId, blogName);
        Member member = memberQueryRepository.getById(memberId);
        post.validateWriter(member);
        List<PostViewStatisticResponse> responses = PeriodPartitioner.partition(cond)
                .map(PostViewStatisticResponse::from)
                .toList();
        List<PostViewStatistic> postViewStatistics = postViewStatisticQueryRepository.findByPost(post, cond);
        accumulate(
                postViewStatistics,
                responses,
                (statistic, response) -> response.addViewCount(statistic.getCount())
        );
        return responses;
    }

    private <S extends CommonStatistic, R extends CommonStatisticResponse> void accumulate(
            List<S> statistics,
            List<R> responses,
            BiConsumer<S, R> accumulator
    ) {
        Deque<S> statisticsDeque = new ArrayDeque<>(statistics);
        for (R response : responses) {
            LocalDate startDate = response.getStartDateInclude();
            LocalDate endDate = response.getEndDateInclude();
            while (!statisticsDeque.isEmpty()) {
                S statistic = statisticsDeque.peekFirst();
                if (!LocalDateUtils.isBetween(startDate, endDate, statistic.getStatisticDate())) {
                    break;
                }
                accumulator.accept(statistic, response);
                statisticsDeque.pollFirst();
            }
            if (statisticsDeque.isEmpty()) {
                return;
            }
        }
    }
}
