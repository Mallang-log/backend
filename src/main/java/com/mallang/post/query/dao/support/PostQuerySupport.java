package com.mallang.post.query.dao.support;

import com.mallang.blog.domain.BlogName;
import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostQuerySupport extends JpaRepository<Post, Long> {

    default Post getById(Long id) {
        return findById(id).orElseThrow(NotFoundPostException::new);
    }

    default Post getByBlogNameAndId(BlogName blogName, Long id) {
        return findByBlogNameAndId(blogName, id).orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.blog.name = :blogName")
    Optional<Post> findByBlogNameAndId(BlogName blogName, Long id);
}
