package com.mallang.comment.query.supoort;

import com.mallang.comment.domain.Comment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentQuerySupport extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(Long postId);
}
