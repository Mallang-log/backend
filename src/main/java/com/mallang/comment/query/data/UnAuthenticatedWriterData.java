package com.mallang.comment.query.data;

import static lombok.AccessLevel.PRIVATE;

import com.mallang.comment.domain.writer.UnAuthenticatedWriter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = PRIVATE)
public final class UnAuthenticatedWriterData implements CommentWriterData {

    private String type = UNAUTHENTICATED_WRITER_DATA_TYPE;
    private String nickname;

    @Builder
    public UnAuthenticatedWriterData(String nickname) {
        this.nickname = nickname;
    }

    public static UnAuthenticatedWriterData from(UnAuthenticatedWriter writer) {
        return UnAuthenticatedWriterData.builder()
                .nickname(writer.getNickname())
                .build();
    }
}
