package com.mallang.subscribe.domain;

import com.mallang.blog.domain.Blog;
import com.mallang.member.domain.Member;
import com.mallang.subscribe.exception.NotFoundBlogSubscribeException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogSubscribeRepository extends JpaRepository<BlogSubscribe, Long> {

    @Override
    default BlogSubscribe getById(Long id) {
        return findById(id).orElseThrow(() -> new NotFoundBlogSubscribeException(id));
    }

    Optional<BlogSubscribe> findBySubscriberIdAndBlogId(Long subscriberId, Long blogId);

    boolean existsBySubscriberAndBlog(Member subscriber, Blog blog);
}
