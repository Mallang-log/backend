package com.mallang.comment.domain.writer;

public record UnAuthenticatedWriterCredential(
        String password
) implements WriterCredential {
}
