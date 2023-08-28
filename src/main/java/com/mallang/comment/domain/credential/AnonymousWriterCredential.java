package com.mallang.comment.domain.credential;

public record AnonymousWriterCredential(
        String password
) implements WriterCredential {
}
