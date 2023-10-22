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
        List<CommentData> result = commentDataDao.findCommentsByPostId(postId).stream()
                .map(this::toDeletedIfRequired)
                .toList();
        if (isPostWriter(postId, memberId)) {
            return result;
        }
        return result.stream()
                .map(data -> toSecretIfRequired(data, memberId))
                .toList();
    }

    private CommentData toDeletedIfRequired(CommentData data) {
        if (data.deleted()) {
            return CommentData.builder()
                    .id(data.id())
                    .content("삭제된 댓글입니다.")
                    .secret(data.secret())
                    .commentWriterData(AuthenticatedWriterData.anonymous())
                    .createdDate(data.createdDate())
                    .deleted(true)
                    .children(data.children())
                    .build();
        }
        return data;
    }

    private boolean isPostWriter(Long postId, Long memberId) {
        Post post = postRepository.getById(postId);
        return Objects.equals(post.getMember().getId(), memberId);
    }

    private CommentData toSecretIfRequired(CommentData data, Long memberId) {
        if (!data.secret()) {
            return data;
        }
        if (data.commentWriterData() instanceof AuthenticatedWriterData authWriter
                && authWriter.getMemberId().equals(memberId)) {
            return data;
        }
        return CommentData.builder()
                .id(data.id())
                .content("비밀 댓글입니다.")
                .secret(true)
                .commentWriterData(AuthenticatedWriterData.anonymous())
                .createdDate(data.createdDate())
                .deleted(data.deleted())
                .children(data.children())
                .build();
    }
}
