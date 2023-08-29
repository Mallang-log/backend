package com.mallang.comment.query.dao;

import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.repository.CommentQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Component
public class CommentDataDao {

    private final CommentQueryRepository commentQueryRepository;

    public List<CommentData> findCommentsByPostId(Long postId) {
        return commentQueryRepository.findCommentsByPostId(postId).stream()
                .map(CommentData::from)
                .toList();
    }
}
