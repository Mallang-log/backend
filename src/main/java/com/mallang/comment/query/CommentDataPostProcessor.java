package com.mallang.comment.query;

import static com.mallang.comment.query.data.AuthenticatedCommentData.WriterData.ANONYMOUS;

import com.mallang.comment.query.data.AuthenticatedCommentData;
import com.mallang.comment.query.data.CommentData;
import com.mallang.comment.query.data.UnAuthenticatedCommentData;
import java.util.List;

public class CommentDataPostProcessor {

    public static List<CommentData> processDeleted(List<CommentData> datas) {
        return datas.stream()
                .map(CommentDataPostProcessor::processDeleted)
                .toList();
    }

    private static CommentData processDeleted(CommentData data) {
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

    public static List<CommentData> processSecret(List<CommentData> datas, Long memberId) {
        return datas.stream()
                .map(it -> processSecret(it, memberId))
                .toList();
    }

    private static CommentData processSecret(CommentData data, Long memberId) {
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
