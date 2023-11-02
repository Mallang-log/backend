package com.mallang.subscribe.query.dao;

import static com.mallang.member.domain.QMember.member;
import static com.mallang.subscribe.domain.QBlogSubscribe.blogSubscribe;

import com.mallang.subscribe.query.data.SubscriberData;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SubscriberDataDao {

    private final JPAQueryFactory query;

    public List<SubscriberData> findSubscribers(
            Long blogId
    ) {
        return query.select(Projections.constructor(SubscriberData.class,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        blogSubscribe.createdDate
                ))
                .from(blogSubscribe)
                .join(blogSubscribe.subscriber, member)
                .where(
                        blogSubscribe.blog.id.eq(blogId)
                )
                .orderBy(blogSubscribe.createdDate.desc())
                .fetch();
    }
}
