package com.mallang.comment.domain;

import com.mallang.comment.exception.NotFoundCommentException;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostId;
import jakarta.annotation.Nullable;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    default AuthComment getAuthCommentById(Long id) {
        return (AuthComment) getById(id);
    }

    default UnAuthComment getUnAuthCommentById(Long id) {
        return (UnAuthComment) getById(id);
    }

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(NotFoundCommentException::new);
    }

    @Override
    @EntityGraph(attributePaths = {"parent"})
    Optional<Comment> findById(Long id);

    default Comment getParentByIdAndPost(@Nullable Long parentCommentId, Post post) {
        if (parentCommentId == null) {
            return null;
        }
        return findByIdAndPost(parentCommentId, post)
                .orElseThrow(NotFoundCommentException::new);
    }

    Optional<Comment> findByIdAndPost(Long id, Post post);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.post.postId = :postId")
    void deleteAllByPostId(@Param("postId") PostId postId);
}
