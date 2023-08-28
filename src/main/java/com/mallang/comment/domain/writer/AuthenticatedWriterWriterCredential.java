package com.mallang.comment.domain.writer;

public record AuthenticatedWriterWriterCredential(
        Long memberId
) implements WriterCredential {
}
