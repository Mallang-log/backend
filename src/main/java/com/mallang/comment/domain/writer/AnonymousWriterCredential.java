package com.mallang.comment.domain.writer;

public record AnonymousWriterCredential(
        String password
) implements WriterCredential {
}
