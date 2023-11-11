package com.mallang.comment.query;

import com.mallang.comment.query.dao.CommentDataDao;
import com.mallang.comment.query.data.CommentData;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentQueryService {

    private final CommentDataDao commentDataDao;
    private final CommentDataValidator commentDataValidator;
    private final CommentDataPostProcessor commentDataPostProcessor;

    public List<CommentData> findAllByPostId(Long postId,
                                             @Nullable Long memberId,
                                             @Nullable String postPassword) {
        commentDataValidator.validateAccessPost(postId, memberId, postPassword);
        List<CommentData> result = commentDataDao.findAllByPostId(postId);
        List<CommentData> deletedProcessedResult = commentDataPostProcessor.processDeleted(result);
        return commentDataPostProcessor.processSecret(postId, deletedProcessedResult, memberId);
    }
}
