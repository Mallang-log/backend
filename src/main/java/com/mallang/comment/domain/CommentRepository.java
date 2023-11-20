package com.mallang.comment.domain;

import com.mallang.comment.exception.NotFoundCommentException;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default AuthenticatedComment getAuthenticatedCommentById(Long id) {
        return (AuthenticatedComment) getById(id);
    }

    default UnAuthenticatedComment getUnAuthenticatedCommentById(Long id) {
        return (UnAuthenticatedComment) getById(id);
    }

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundCommentException::new);
    }

    default Comment getByIdAndPostId(Long id, Long postId) {
        return findByIdAndPostId(id, postId)
                .orElseThrow(NotFoundCommentException::new);
    }

    Optional<Comment> findByIdAndPostId(Long id, Long postId);

    @Override
    @EntityGraph(attributePaths = {"parent"})
    Optional<Comment> findById(Long id);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteAllByPostId(Long postId);
}
