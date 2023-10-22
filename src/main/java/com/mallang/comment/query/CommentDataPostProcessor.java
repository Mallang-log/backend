package com.mallang.comment.query;

import com.mallang.comment.query.data.AuthenticatedWriterData;
import com.mallang.comment.query.data.CommentData;
import java.util.List;

public class CommentDataPostProcessor {

    public static List<CommentData> processDeleted(List<CommentData> datas) {
        return datas.stream()
                .map(CommentDataPostProcessor::processDeleted)
                .toList();
    }

    private static CommentData processDeleted(CommentData data) {
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

    public static List<CommentData> processSecret(List<CommentData> datas, Long memberId) {
        return datas.stream()
                .map(it -> processSecret(it, memberId))
                .toList();
    }

    private static CommentData processSecret(CommentData data, Long memberId) {
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
