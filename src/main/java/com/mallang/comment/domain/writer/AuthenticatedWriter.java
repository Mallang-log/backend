package com.mallang.comment.domain.writer;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.mallang.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@Entity
public class AuthenticatedWriter extends CommentWriter {

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @Override
    public boolean hasAuthority(WriterCredential writerCredential) {
        if (writerCredential instanceof AuthenticatedWriterCredential authenticatedWriterCredential) {
            return member.getId().equals(authenticatedWriterCredential.memberId());
        }
        return false;
    }

    @Override
    public boolean canWriteSecret() {
        return true;
    }
}
