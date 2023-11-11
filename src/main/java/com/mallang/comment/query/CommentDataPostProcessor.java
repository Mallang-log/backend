package com.mallang.comment.query;

import static com.mallang.comment.query.data.AuthenticatedCommentData.WriterData.ANONYMOUS;

import com.mallang.comment.query.data.AuthenticatedCommentData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.UnAuthenticatedCommentData;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDataPostProcessor {

    private final PostRepository postRepository;

    public List<CommentData> processDeleted(List<CommentData> datas) {
        return datas.stream()
                .map(this::processDeleted)
                .toList();
    }

    private CommentData processDeleted(CommentData data) {
        if (!data.isDeleted()) {
            return data;
        }
        if (data instanceof UnAuthenticatedCommentData unAuth) {
            return UnAuthenticatedCommentData.builder()
                    .id(data.getId())
                    .content("삭제된 댓글입니다.")
                    .writerData(unAuth.getWriterData())
                    .createdDate(unAuth.getCreatedDate())
                    .deleted(unAuth.isDeleted())
                    .children(unAuth.getChildren())
                    .build();
        }
        if (data instanceof AuthenticatedCommentData authed) {
            return AuthenticatedCommentData.builder()
                    .id(data.getId())
                    .content("삭제된 댓글입니다.")
                    .secret(authed.isSecret())
                    .writerData(authed.getWriterData())
                    .createdDate(authed.getCreatedDate())
                    .deleted(authed.isDeleted())
                    .children(authed.getChildren())
                    .build();
        }
        throw new RuntimeException("CommentDataPostProcessor에서 처리되지 않는 형식의 댓글이 들어왔습니다.");
    }

    public List<CommentData> processSecret(Long postId, List<CommentData> datas, Long memberId) {
        if (isPostWriter(postId, memberId)) {
            return datas;
        }
        return datas.stream()
                .map(it -> processSecret(it, memberId))
                .toList();
    }

    private boolean isPostWriter(Long postId, Long memberId) {
        Post post = postRepository.getById(postId);
        return Objects.equals(post.getWriter().getId(), memberId);
    }

    private CommentData processSecret(CommentData data, Long memberId) {
        if (data instanceof UnAuthenticatedCommentData) {
            return data;
        }
        AuthenticatedCommentData authed = (AuthenticatedCommentData) data;
        if (!authed.isSecret()) {
            return data;
        }
        if (authed.getWriterData().memberId().equals(memberId)) {
            return data;
        }
        return AuthenticatedCommentData.builder()
                .id(data.getId())
                .content("비밀 댓글입니다.")
                .secret(true)
                .writerData(ANONYMOUS)
                .createdDate(authed.getCreatedDate())
                .deleted(authed.isDeleted())
                .children(authed.getChildren())
                .build();
    }
}
