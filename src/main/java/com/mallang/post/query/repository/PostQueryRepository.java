package com.mallang.post.query.repository;

import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import com.mallang.post.exception.NotFoundPostException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostQueryRepository extends
        JpaRepository<Post, PostId>,
        PostManageSearchDao,
        PostSearchDao {

    default Post getById(Long id, String blogName) {
        return findById(id, blogName)
                .orElseThrow(NotFoundPostException::new);
    }

    @Query("SELECT p FROM Post p WHERE p.id.postId = :id AND p.blog.name.value = :blogName")
    Optional<Post> findById(
            @Param("id") Long id,
            @Param("blogName") String blogName
    );

    default Post getByPostIdAndBlogNameAndWriterId(Long postId, String blogName, Long writerId) {
        return findByPostIdAndBlogNameAndWriterId(postId, blogName, writerId)
                .orElseThrow(NotFoundPostException::new);
    }

    @Query("""
            SELECT p FROM  Post p
            WHERE p.id.postId = :id
            AND p.blog.name.value = :blogName
            AND p.content.writer.id = :writerId
            """)
    Optional<Post> findByPostIdAndBlogNameAndWriterId(
            @Param("id") Long id,
            @Param("blogName") String blogName,
            @Param("writerId") Long writerId
    );
}
