package com.mallang.comment.query;

import static com.mallang.comment.query.response.AuthCommentResponse.WriterResponse.ANONYMOUS;

import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import com.mallang.post.domain.Post;
import com.mallang.post.domain.PostRepository;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDataPostProcessor {

    private final PostRepository postRepository;

    public List<CommentResponse> processDeleted(List<CommentResponse> datas) {
        return datas.stream()
                .map(this::processDeleted)
                .toList();
    }

    private CommentResponse processDeleted(CommentResponse data) {
        if (!data.isDeleted()) {
            return data;
        }
        if (data instanceof UnAuthCommentResponse unAuth) {
            return UnAuthCommentResponse.builder()
                    .id(data.getId())
                    .content("삭제된 댓글입니다.")
                    .writer(unAuth.getWriter())
                    .createdDate(unAuth.getCreatedDate())
                    .deleted(unAuth.isDeleted())
                    .children(unAuth.getChildren())
                    .build();
        }
        AuthCommentResponse authed = (AuthCommentResponse) data;
        return AuthCommentResponse.builder()
                .id(data.getId())
                .content("삭제된 댓글입니다.")
                .secret(authed.isSecret())
                .writer(authed.getWriter())
                .createdDate(authed.getCreatedDate())
                .deleted(authed.isDeleted())
                .children(authed.getChildren())
                .build();
    }

    public List<CommentResponse> processSecret(List<CommentResponse> datas, Long postId, @Nullable Long memberId) {
        if (isPostWriter(postId, memberId)) {
            return datas;
        }
        return datas.stream()
                .map(it -> processSecret(it, memberId))
                .toList();
    }

    private boolean isPostWriter(Long postId, @Nullable Long memberId) {
        Post post = postRepository.getById(postId);
        return Objects.equals(post.getWriter().getId(), memberId);
    }

    private CommentResponse processSecret(CommentResponse data, @Nullable Long memberId) {
        if (data instanceof UnAuthCommentResponse) {
            return data;
        }
        AuthCommentResponse authed = (AuthCommentResponse) data;
        if (!authed.isSecret()) {
            return data;
        }
        if (authed.getWriter().memberId().equals(memberId)) {
            return data;
        }
        return AuthCommentResponse.builder()
                .id(data.getId())
                .content("비밀 댓글입니다.")
                .secret(true)
                .writer(ANONYMOUS)
                .createdDate(authed.getCreatedDate())
                .deleted(authed.isDeleted())
                .children(authed.getChildren().stream()
                        .map(it -> processSecret(it, memberId))
                        .toList())
                .build();
    }
}
