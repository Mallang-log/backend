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
@DiscriminatorValue("AnonymousWriter")
@Entity
public class AnonymousWriter extends CommentWriter {

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Override
    public boolean hasAuthority(WriterCredential writerCredential) {
        if (writerCredential instanceof AnonymousWriterCredential anonymousWriterCredential) {
            return password.equals(anonymousWriterCredential.password());
        }
        return false;
    }
}
