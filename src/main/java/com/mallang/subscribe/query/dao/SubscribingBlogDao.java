package com.mallang.subscribe.query.dao;

import static com.mallang.auth.domain.QMember.member;
import static com.mallang.blog.domain.QBlog.blog;
import static com.mallang.subscribe.domain.QBlogSubscribe.blogSubscribe;

import com.mallang.subscribe.query.response.SubscribingBlogResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class SubscribingBlogDao {

    private final JPAQueryFactory query;

    public List<SubscribingBlogResponse> findSubscribingBlogs(
            Long memberId
    ) {
        return query.select(Projections.constructor(SubscribingBlogResponse.class,
                        blog.id,
                        blog.name.value,
                        member.id,
                        member.nickname,
                        member.profileImageUrl,
                        blogSubscribe.createdDate
                ))
                .from(blogSubscribe)
                .join(blogSubscribe.blog, blog)
                .join(blogSubscribe.subscriber, member)
                .where(blogSubscribe.subscriber.id.eq(memberId))
                .orderBy(blogSubscribe.createdDate.desc())
                .fetch();
    }
}
