package com.mallang.comment.query.dao;

import com.mallang.comment.query.dao.supoort.CommentQuerySupport;
import com.mallang.comment.query.data.CommentData;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CommentDataDao {

    private final CommentQuerySupport commentQuerySupport;

    public List<CommentData> findCommentsByPostId(Long postId) {
        return commentQuerySupport.findCommentsByPostId(postId).stream()
                .filter(it -> Objects.isNull(it.getParent()))
                .map(CommentData::from)
                .toList();
    }
}
