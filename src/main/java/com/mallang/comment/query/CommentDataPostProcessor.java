package com.mallang.comment.query;

import static com.mallang.comment.query.response.AuthCommentResponse.WriterResponse.ANONYMOUS;

import com.mallang.comment.query.response.AuthCommentResponse;
import com.mallang.comment.query.response.CommentResponse;
import com.mallang.comment.query.response.UnAuthCommentResponse;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentDataPostProcessor {

    public List<CommentResponse> processSecret(List<CommentResponse> datas,
                                               @Nullable Long memberId) {
        return datas.stream()
                .map(it -> processSecret(it, memberId))
                .toList();
    }

    private CommentResponse processSecret(CommentResponse data, @Nullable Long memberId) {
        if (data instanceof UnAuthCommentResponse) {
            return data;
        }
        AuthCommentResponse authed = (AuthCommentResponse) data;
        if (!authed.isSecret() || authed.getWriter().memberId().equals(memberId)) {
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
