package com.mallang.reference.domain;

import static lombok.AccessLevel.PROTECTED;

import com.mallang.reference.exception.BadReferenceLinkMemoException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PROTECTED)
@Getter
@Embeddable
public class ReferenceLinkMemo {

    private static final int MEMO_MAX_LENGTH = 300;

    @Column(length = MEMO_MAX_LENGTH)
    private String memo;

    public ReferenceLinkMemo(String memo) {
        validate(memo);
        this.memo = memo;
    }

    private void validate(String memo) {
        if (memo == null) {
            return;
        }
        if (memo.length() > MEMO_MAX_LENGTH) {
            throw new BadReferenceLinkMemoException(MEMO_MAX_LENGTH);
        }
    }
}
