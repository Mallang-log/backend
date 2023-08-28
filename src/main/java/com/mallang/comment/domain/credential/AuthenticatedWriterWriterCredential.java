package com.mallang.comment.domain.credential;

public record AuthenticatedWriterWriterCredential(
        Long memberId
) implements WriterCredential {
}
