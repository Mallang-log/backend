package com.mallang.comment.query.data;

import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import lombok.Builder;

@Builder
public record UnAuthenticatedWriterData(
        String nickname
) implements CommentWriterData {

    public static UnAuthenticatedWriterData from(UnAuthenticatedWriter writer) {
        return UnAuthenticatedWriterData.builder()
                .nickname(writer.getNickname())
                .build();
    }
}
