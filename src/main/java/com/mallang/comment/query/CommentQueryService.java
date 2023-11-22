package com.mallang.comment.query;

import com.mallang.comment.query.dao.CommentDao;
import com.mallang.comment.query.response.CommentResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentQueryService {

    private final CommentDao commentDao;
    private final CommentDataValidator commentDataValidator;
    private final CommentDataPostProcessor commentDataPostProcessor;

    public List<CommentResponse> findAllByPostId(Long postId,
                                                 @Nullable Long memberId,
                                                 @Nullable String postPassword) {
        commentDataValidator.validateAccessPost(postId, memberId, postPassword);
        List<CommentResponse> result = commentDao.findAllByPostId(postId);
        List<CommentResponse> deletedProcessedResult = commentDataPostProcessor.processDeleted(result);
        return commentDataPostProcessor.processSecret(deletedProcessedResult, postId, memberId);
    }
}
