package com.mallang.comment.query;

import static com.mallang.comment.query.CommentDataPostProcessor.processDeleted;
import static com.mallang.comment.query.CommentDataPostProcessor.processSecret;

import com.mallang.comment.query.dao.CommentDataDao;
import com.mallang.comment.query.data.CommentData;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentQueryService {

    private final PostRepository postRepository;
    private final CommentDataDao commentDataDao;

    public List<CommentData> findAllByPostId(Long postId, @Nullable Long memberId) {
        List<CommentData> deletedProcessedResult = processDeleted(commentDataDao.findAllByPostId(postId));
        if (isPostWriter(postId, memberId)) {
            return deletedProcessedResult;
        }
        return processSecret(deletedProcessedResult, memberId);
    }

    private boolean isPostWriter(Long postId, Long memberId) {
        Post post = postRepository.getById(postId);
        return Objects.equals(post.getWriter().getId(), memberId);
    }
}
