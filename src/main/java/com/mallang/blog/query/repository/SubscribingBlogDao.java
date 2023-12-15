package com.mallang.blog.query.repository;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.blog.domain.QBlog.blog;
import static com.mallang.blog.domain.subscribe.QBlogSubscribe.blogSubscribe;

import com.mallang.blog.domain.subscribe.BlogSubscribe;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

public interface SubscribingBlogDao {

    Page<BlogSubscribe> findSubscribingBlogs(Long memberId, Pageable pageable);

    @RequiredArgsConstructor
    @Repository
    class SubscribingBlogDaoImpl implements SubscribingBlogDao {

        private final JPAQueryFactory query;

        @Override
        public Page<BlogSubscribe> findSubscribingBlogs(Long memberId, Pageable pageable) {
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
            return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        }
    }
}
