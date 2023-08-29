package com.mallang.comment.query;

import com.mallang.comment.query.dao.CommentDataDao;
import com.mallang.comment.query.data.AuthenticatedWriterData;
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
        if (isPostWriter(postId, memberId)) {
            return commentDataDao.findCommentsByPostId(postId);
        }
        return commentDataDao.findCommentsByPostId(postId).stream()
                .filter(data -> hasAuthorityToSecret(data, memberId))
                .toList();
    }

    private boolean isPostWriter(Long postId, Long memberId) {
        Post post = postRepository.getById(postId);
        return Objects.equals(post.getMember().getId(), memberId);
    }

    private boolean hasAuthorityToSecret(CommentData data, Long memberId) {
        if (!data.secret()) {
            return true;
        }
        AuthenticatedWriterData authenticatedWriterData = (AuthenticatedWriterData) data.commentWriterData();
        return Objects.equals(authenticatedWriterData.memberId(), memberId);
    }
}
