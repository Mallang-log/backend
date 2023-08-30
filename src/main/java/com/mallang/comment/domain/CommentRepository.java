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
    //@Query("select c from Comment c join fetch c.commentWriter cw left join fetch c.parent p left join fetch p.commentWriter pcw where c.id = :id")
    @EntityGraph(attributePaths = {"commentWriter", "parent", "parent.commentWriter"})
    Optional<Comment> findById(Long id);
}
