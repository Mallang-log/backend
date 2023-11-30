package com.mallang.blog.query.repository;


import com.mallang.blog.domain.subscribe.BlogSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogSubscribeQueryRepository extends
        JpaRepository<BlogSubscribe, Long>,
        SubscriberDao,
        SubscribingBlogDao {
}
