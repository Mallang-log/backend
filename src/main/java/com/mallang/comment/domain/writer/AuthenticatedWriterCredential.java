package com.mallang.comment.domain.writer;

public record AuthenticatedWriterCredential(
        Long memberId
) implements WriterCredential {
}
