package com.mallang.comment.domain;

import com.mallang.comment.exception.NotFoundCommentException;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundCommentException::new);
    }

    @Override
    @EntityGraph(attributePaths = {"commentWriter", "parent"})
    Optional<Comment> findById(Long id);
}
