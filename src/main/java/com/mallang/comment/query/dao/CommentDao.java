package com.mallang.comment.query.dao;

import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.supoort.CommentQuerySupport;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CommentDao {

    private final CommentQuerySupport commentQuerySupport;

    public List<CommentResponse> findAllByPostId(Long postId) {
        return commentQuerySupport.findAllByPostId(postId).stream()
                .filter(it -> Objects.isNull(it.getParent()))
                .map(CommentResponse::from)
                .toList();
    }
}
