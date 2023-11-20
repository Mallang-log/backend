package com.mallang.subscribe.domain;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.subscribe.exception.NotFoundBlogSubscribeException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlogSubscribeRepository extends JpaRepository<BlogSubscribe, Long> {

    @Override
    default BlogSubscribe getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundBlogSubscribeException(id));
    }

    @Query("SELECT bs FROM BlogSubscribe bs WHERE bs.subscriber.id = :subscriberId AND bs.blog.name.value = :blogName")
    Optional<BlogSubscribe> findBySubscriberIdAndBlogName(
            @Param("subscriberId") Long subscriberId,
            @Param("blogName") String blogName
    );

    boolean existsBySubscriberAndBlog(Member subscriber, Blog blog);
}
