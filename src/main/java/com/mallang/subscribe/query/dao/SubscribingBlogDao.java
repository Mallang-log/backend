package com.mallang.subscribe.query.dao;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.blog.domain.QBlog.blog;
import static com.mallang.subscribe.domain.QBlogSubscribe.blogSubscribe;

import com.mallang.subscribe.domain.BlogSubscribe;
import com.mallang.subscribe.query.response.SubscribingBlogResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SubscribingBlogDao {

    private final JPAQueryFactory query;

    public Page<SubscribingBlogResponse> findSubscribingBlogs(Long memberId, Pageable pageable) {
        JPAQuery<Long> countQuery = query.select(blogSubscribe.countDistinct())
                .from(blogSubscribe)
                .where(blogSubscribe.subscriber.id.eq(memberId));
        List<BlogSubscribe> result = query.selectFrom(blogSubscribe)
                .distinct()
                .join(blogSubscribe.blog, blog).fetchJoin()
                .join(blogSubscribe.subscriber, member).fetchJoin()
                .where(blogSubscribe.subscriber.id.eq(memberId))
                .orderBy(blogSubscribe.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne)
                .map(SubscribingBlogResponse::from);
    }
}