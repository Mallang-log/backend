package com.mallang.blog.query.repository;

import static com.mallang.auth.domain.QMember.member;
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

public interface SubscriberDao {

    Page<BlogSubscribe> findSubscribers(String blogName, Pageable pageable);

    @RequiredArgsConstructor
    @Repository
    class SubscriberDaoImpl implements SubscriberDao {

        private final JPAQueryFactory query;

        @Override
        public Page<BlogSubscribe> findSubscribers(String blogName, Pageable pageable) {
            JPAQuery<Long> countQuery = query.select(blogSubscribe.countDistinct())
                    .from(blogSubscribe)
                    .where(blogSubscribe.blog.name.value.eq(blogName));
            List<BlogSubscribe> result = query.selectFrom(blogSubscribe)
                    .distinct()
                    .join(blogSubscribe.subscriber, member).fetchJoin()
                    .where(blogSubscribe.blog.name.value.eq(blogName))
                    .orderBy(blogSubscribe.createdDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
            return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        }
    }
}
