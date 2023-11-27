package com.mallang.post.query.support;

import com.mallang.post.domain.Post;
import com.mallang.post.exception.NotFoundPostException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostQuerySupport extends JpaRepository<Post, Long> {

    default Post getByIdAndBlogName(Long id, String blogName) {
        return findByIdAndBlogName(id, blogName)
                .orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.postId.id = :id AND p.blog.name.value = :blogName")
    Optional<Post> findByIdAndBlogName(@Param("id") Long id,
                                       @Param("blogName") String blogName);

    default Post getByPostIdAndBlogNameAndWriterId(Long id, String blogName, Long writerId) {
        return findByPostIdAndBlogNameAndWriterId(id, blogName, writerId).orElseThrow(NotFoundPostException::new);
    }

    @Query("""
            SELECT p FROM  Post p
            WHERE p.postId.id = :id
            AND p.blog.name.value = :blogName
            AND p.writer.id = :writerId
            """)
    Optional<Post> findByPostIdAndBlogNameAndWriterId(
            @Param("id") Long id,
            @Param("blogName") String blogName,
            @Param("writerId") Long writerId
    );
}
