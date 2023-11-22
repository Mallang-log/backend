package com.mallang.subscribe.query.dao;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.subscribe.domain.QBlogSubscribe.blogSubscribe;

import com.mallang.subscribe.query.response.SubscriberResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SubscriberDao {

    private final JPAQueryFactory query;

    public List<SubscriberResponse> findSubscribers(
            String blogName
    ) {
        return query.select(Projections.constructor(SubscriberResponse.class,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        blogSubscribe.createdDate
                ))
                .from(blogSubscribe)
                .join(blogSubscribe.subscriber, member)
                .where(
                        blogSubscribe.blog.name.value.eq(blogName)
                )
                .orderBy(blogSubscribe.createdDate.desc())
                .fetch();
    }
}
