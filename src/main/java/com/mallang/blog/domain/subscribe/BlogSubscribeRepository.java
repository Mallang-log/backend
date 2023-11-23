package com.mallang.blog.domain.subscribe;

import com.mallang.auth.domain.Member;
import com.mallang.blog.domain.Blog;
import com.mallang.blog.exception.NotFoundBlogSubscribeException;
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
