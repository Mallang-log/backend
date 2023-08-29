package com.mallang.comment.query.repository;

import com.mallang.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentQueryRepository extends JpaRepository<Comment, Long>, CommentQueryDslRepository {

    @EntityGraph(attributePaths = "commentWriter")
    List<Comment> findCommentsByPostId(Long postId);
}
