package com.mallang.comment.domain.writer;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@DiscriminatorValue("UnAuthenticatedWriter")
@Entity
public class UnAuthenticatedWriter extends CommentWriter {

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Override
    public boolean hasAuthority(WriterCredential writerCredential) {
        if (writerCredential instanceof UnAuthenticatedWriterCredential unAuthenticatedWriterCredential) {
            return password.equals(unAuthenticatedWriterCredential.password());
        }
        return false;
    }

    @Override
    public boolean canWriteSecret() {
        return false;
    }
}
