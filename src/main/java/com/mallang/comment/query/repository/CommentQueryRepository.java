package com.mallang.comment.query.repository;

import com.mallang.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentQueryRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.postId.id = :postId AND c.post.blog.name.value = :blogName")
    List<Comment> findAllByPost(@Param("postId") Long postId, @Param("blogName") String blogName);
}
