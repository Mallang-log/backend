package com.mallang.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.comment.domain.credential.AuthenticatedWriterWriterCredential;
import com.mallang.comment.domain.credential.WriterCredential;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
public class AuthenticatedWriter extends CommentWriter {

    @Column(nullable = false, unique = true)
    private Long memberId;

    @Override
    public boolean hasAuthority(WriterCredential writerCredential) {
        if (writerCredential instanceof AuthenticatedWriterWriterCredential authenticatedWriterCredential) {
            return memberId.equals(authenticatedWriterCredential.memberId());
        }
        return false;
    }
}
