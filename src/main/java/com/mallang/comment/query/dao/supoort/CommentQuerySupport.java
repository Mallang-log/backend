package com.mallang.comment.query.dao.supoort;

import com.mallang.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentQuerySupport extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "commentWriter")
    List<Comment> findCommentsByPostId(Long postId);
}
